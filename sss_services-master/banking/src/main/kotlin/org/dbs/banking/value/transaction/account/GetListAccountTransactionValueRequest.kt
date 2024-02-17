package org.dbs.banking.value.transaction.account

import kotlinx.coroutines.runBlocking
import org.dbs.application.core.service.funcs.IntFuncs.pagesCount
import org.dbs.application.core.service.funcs.IntFuncs.thereX
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.application.core.service.funcs.Patterns.BANK_ACCOUNT_PATTERN_LIGHT_MASK
import org.dbs.application.core.service.funcs.Patterns.DATE_TIME_PATTERN
import org.dbs.application.core.service.funcs.Patterns.MONEY_PATTERN
import org.dbs.application.core.service.funcs.Patterns.STATUS_NAME_PATTERN
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.banking.service.AccountTransactionService
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4ListDto
import org.dbs.banking.transactions.dto.transaction.account.AccountTransactionListDto
import org.dbs.banking.transactions.dto.transaction.account.GetAccountTransactionListResponse
import org.dbs.consts.AccountNum
import org.dbs.consts.RestHttpConsts.RestQueryParams.AccountParams.QP_ACCOUNT_CODE_MASK
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.ACC_TRANSACTION_DEFAULT_SORT_FIELD
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.ACC_TRANSACTION_DEFAULT_SORT_ORDER
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_DATE_FROM
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_DATE_TO
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_STATUS_NAME_MASK
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_SUM
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_TYPE_NAME_MASK
import org.dbs.consts.SysConst.MAX_DATE_TIME
import org.dbs.consts.SysConst.MIN_DATE_TIME
import org.dbs.consts.TransactionKindName
import org.dbs.consts.TransactionStatusName
import org.dbs.ext.FluxFuncs.noEmpty
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_ACCOUNT_TRANSACTION_SUM
import org.dbs.rest.api.nio.FieldValidators.validateMoney
import org.dbs.rest.api.nio.QueryParamValidators.validatePageNum
import org.dbs.rest.api.nio.QueryParamValidators.validatePageSize
import org.dbs.rest.api.nio.QueryParamValidators.validateParamPatternIfPresent
import org.dbs.rest.api.nio.QueryParamValidators.validateSortField
import org.dbs.rest.api.nio.QueryParamValidators.validateSortOrder
import org.dbs.rest.api.nio.ResponseFuncsExt.suspendFinishHttpResponse
import org.dbs.rest.api.nio.ResponseFuncsExt.suspendWhenNoErrors
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.dbs.validator.Field.SSS_ACCOUNT_TRANSACTION_DATE
import org.dbs.validator.Field.SSS_ACCOUNT_TRANSACTION_KIND
import org.dbs.validator.Field.SSS_ACCOUNT_TRANSACTION_STATUS
import org.dbs.validator.Field.SSS_ACCOUNT_TRANSACTION_SUM
import org.dbs.validator.Field.SSS_CUSTOMER_ACCOUNT_CODE
import org.springframework.data.domain.PageRequest
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import kotlin.properties.Delegates.notNull

@JvmInline
value class GetListAccountTransactionValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>,
    HttpReactiveGetRequest<AccountTransactionListDto, GetAccountTransactionListResponse> {

    suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
        accountTransactionService: AccountTransactionService,
    ) = processor.createResponse(
        serverRequest,
        GetAccountTransactionListResponse::class.java
    ) {
        serverRequest.run {
            runBlocking {
                accountTransactionService.getListAccountTransaction(
                    GetAccountTransactionListResponse(this@run.id())
                )
            }
        }
    }

    private suspend fun AccountTransactionService.getListAccountTransaction(
        response: GetAccountTransactionListResponse
    ): Mono<GetAccountTransactionListResponse> = buildMonoResponse(serverRequest) {
        object {
            var pageSize = 0
            var pageNum = 0
            var sortField = ACC_TRANSACTION_DEFAULT_SORT_FIELD
            var sortOrder = ACC_TRANSACTION_DEFAULT_SORT_ORDER
            var transactionDateFrom = MIN_DATE_TIME
            var transactionDateTo = MAX_DATE_TIME

            // filters
            var filterTransactionTypeNameSearchMask: TransactionKindName? = null
            var filterTransactionStatusNameSearchMask: TransactionStatusName? = null
            var filterTransactionSumSearchMask: String? = null
            var filterAccountCodeSearchMask: AccountNum? = null

            var accountTransactionCount by notNull<Int>()
            var pagesCount by notNull<Int>()
            val accountTransactionDtos = createCollection<AccountTransaction4ListDto>()

            fun validateRequest() = response.run {
                validatePageSize(serverRequest, maxPageSize) { pageSize = it }
                validatePageNum(serverRequest, maxPages) { pageNum = it }
                validateSortField(serverRequest) { sortField = it }
                validateSortOrder(serverRequest) { sortOrder = it }

                validateParamPatternIfPresent(
                    serverRequest,
                    QP_TRANSACTION_STATUS_NAME_MASK,
                    STATUS_NAME_PATTERN, SSS_ACCOUNT_TRANSACTION_STATUS
                ) {
                    filterTransactionStatusNameSearchMask = "${it.trim().lowercase()}%"
                }

                validateParamPatternIfPresent(
                    serverRequest,
                    QP_TRANSACTION_TYPE_NAME_MASK,
                    STATUS_NAME_PATTERN, SSS_ACCOUNT_TRANSACTION_KIND
                ) {
                    filterTransactionTypeNameSearchMask = "${it.trim().lowercase()}%"
                }

                validateParamPatternIfPresent(
                    serverRequest,
                    QP_TRANSACTION_DATE_FROM,
                    DATE_TIME_PATTERN, SSS_ACCOUNT_TRANSACTION_DATE
                ) {
                    transactionDateFrom = it.toLong().toLocalDateTime()
                }

                validateParamPatternIfPresent(
                    serverRequest,
                    QP_TRANSACTION_DATE_TO,
                    DATE_TIME_PATTERN, SSS_ACCOUNT_TRANSACTION_DATE
                ) {
                    transactionDateTo= it.toLong().toLocalDateTime()
                }

                validateParamPatternIfPresent(
                        serverRequest,
                        QP_TRANSACTION_SUM,
                        MONEY_PATTERN, SSS_ACCOUNT_TRANSACTION_SUM
                ) {
                    validateMoney(it.toBigDecimal(), OC_INVALID_ACCOUNT_TRANSACTION_SUM, SSS_ACCOUNT_TRANSACTION_SUM)
                    filterTransactionSumSearchMask = "$it%"
                }

                validateParamPatternIfPresent(
                    serverRequest,
                    QP_ACCOUNT_CODE_MASK,
                    BANK_ACCOUNT_PATTERN_LIGHT_MASK, SSS_CUSTOMER_ACCOUNT_CODE
                ) {
                    filterAccountCodeSearchMask = "${it.trim().lowercase()}%"
                }

                toMono()
            }

            //==========================================================================================================
            fun findCountAccountTransactions() = response.run {
                countAllBy(
                    filterAccountCodeSearchMask, filterTransactionSumSearchMask, filterTransactionStatusNameSearchMask,
                    filterTransactionTypeNameSearchMask, transactionDateFrom, transactionDateTo
                ).publishOn(parallelScheduler)
                    .flatMap { fmOrdersCount ->
                        accountTransactionCount = fmOrdersCount.toInt()
                        pagesCount = fmOrdersCount.pagesCount(pageSize)
                        response.toMono()
                    }
            }

            //==========================================================================================================
            fun Mono<GetAccountTransactionListResponse>.findAllAccountTransactions() = flatMap {
                accountTransactionCount.noEmpty {
                    findAllBy(
                        filterAccountCodeSearchMask, filterTransactionSumSearchMask,
                        filterTransactionStatusNameSearchMask, filterTransactionTypeNameSearchMask, transactionDateFrom,
                        transactionDateTo, sortField, sortOrder, PageRequest.of(pageNum - 1, pageSize)
                    )
                }.map(accountTransactionMappers::map2AccountTransaction4ListDto)
                    .collectList()
                    .map {
                        accountTransactionDtos.addAll(it)
                        response
                    }
            }

            suspend fun Mono<GetAccountTransactionListResponse>.finishResponseEntity() =
                this.suspendFinishHttpResponse(response) { resp ->
                    resp.responseEntity = AccountTransactionListDto(
                        count = accountTransactionCount,
                        list = accountTransactionDtos,
                        elements = accountTransactionDtos.size,
                        pages = pagesCount
                    )
                    resp.message = "${accountTransactionDtos.size.thereX()} element(s) in list"
                }
        }.run {
            validateRequest()
                .suspendWhenNoErrors {
                    findCountAccountTransactions()
                        .findAllAccountTransactions()
                        .finishResponseEntity()
                }
        }
    }
}
