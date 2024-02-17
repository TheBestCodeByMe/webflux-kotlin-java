package org.dbs.banking.value.transaction.account

import kotlinx.coroutines.runBlocking
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.NUM_ACCOUNT_TRANSACTION_PATTERN
import org.dbs.banking.service.AccountTransactionService
import org.dbs.banking.transactions.consts.TransactionCode
import org.dbs.banking.transactions.dto.transaction.account.AccountTransactionCardDto
import org.dbs.banking.transactions.dto.transaction.account.GetAccountTransactionCardResponse
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_CODE
import org.dbs.enums.I18NEnum.FLD_UNKNOWN_ACCOUNT_TRANSACTION_CODE
import org.dbs.ref.serv.enums.TransactionKindEnum.INTERNAL_TRANSACTION_INCOME
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_ACCOUNT_TRANSACTION_CODE
import org.dbs.rest.api.nio.QueryParamValidators.validateParamPatternIfPresent
import org.dbs.rest.api.nio.ResponseFuncsExt.suspendFinishHttpResponse
import org.dbs.rest.api.nio.ResponseFuncsExt.suspendWhenNoErrors
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactiveGetRequest
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.SSS_ACCOUNT_TRANSACTION_CODE
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@JvmInline
value class GetCardAccountsTransactionValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>,
    HttpReactiveGetRequest<AccountTransactionCardDto, GetAccountTransactionCardResponse> {

    suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
        accountTransactionService: AccountTransactionService,
    ) = processor.createResponse(
        serverRequest,
        GetAccountTransactionCardResponse::class.java
    ) {
        serverRequest.run {
            runBlocking {
                accountTransactionService.getCardAccountTransaction(
                    GetAccountTransactionCardResponse(this@run.id())
                )
            }
        }
    }

    private suspend fun AccountTransactionService.getCardAccountTransaction(
        response: GetAccountTransactionCardResponse
    ): Mono<GetAccountTransactionCardResponse> = buildMonoResponse(serverRequest) {
        object {
            // filters
            val filterTransactionCode = LateInitVal<TransactionCode>()

            val accountTransactionDto = LateInitVal<AccountTransactionCardDto>()

            fun validateRequest() = response.run {

                validateParamPatternIfPresent(
                    serverRequest,
                    QP_TRANSACTION_CODE,
                    NUM_ACCOUNT_TRANSACTION_PATTERN, SSS_ACCOUNT_TRANSACTION_CODE
                ) {
                    filterTransactionCode.hold(it)
                }

                toMono()
            }

            //==========================================================================================================
            fun findAccountTransaction() = response.run {
                findByCode(
                    filterTransactionCode.value
                ).map {
                    accountTransactionMappers.map2AccountTransaction4CardDto(
                        it,
                        it.transactionTypeId == INTERNAL_TRANSACTION_INCOME.getTransactionKindId()
                    )
                }.map {
                    accountTransactionDto.hold(it)
                    response
                }.switchIfEmpty {
                    response.addErrorInfo(
                        OC_INVALID_ACCOUNT_TRANSACTION_CODE,
                        INVALID_ENTITY_ATTR,
                        SSS_ACCOUNT_TRANSACTION_CODE,
                        findI18nMessage(FLD_UNKNOWN_ACCOUNT_TRANSACTION_CODE) + "('$filterTransactionCode')"
                    )
                    empty()
                }
            }

            suspend fun Mono<GetAccountTransactionCardResponse>.finishResponseEntity() =
                this.suspendFinishHttpResponse(response) { resp ->
                    resp.responseEntity = accountTransactionDto.value
                    resp.message =
                        "Account with transaction code - ${accountTransactionDto.value.transaction.transactionCode} - in response"
                }
        }.run {
            validateRequest()
                .suspendWhenNoErrors {
                    findAccountTransaction()
                        .finishResponseEntity()
                }
        }
    }
}
