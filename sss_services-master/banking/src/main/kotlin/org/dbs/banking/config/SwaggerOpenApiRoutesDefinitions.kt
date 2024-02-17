package org.dbs.banking.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_CREATE_CARD_TRANSACTION_REFILL
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_CREATE_CARD_TRANSACTION_WITHDRAWAL
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_CREATE_INTERNAL_TRANSACTION
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_GET_ACCOUNT_TRANSACTION_CARD
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_GET_ACCOUNT_TRANSACTION_LIST
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_GET_CARD_TRANSACTION_CARD
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_GET_CARD_TRANSACTION_LIST
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Tags.ROUTE_TAG_TRANSACTION
import org.dbs.banking.transactions.dto.transaction.CreateCardTransactionRequest
import org.dbs.banking.transactions.dto.transaction.CreateCardTransactionResponse
import org.dbs.banking.transactions.dto.transaction.CreateCardTransactionWithdrawalRequest
import org.dbs.banking.transactions.dto.transaction.CreateCardTransactionWithdrawalResponse
import org.dbs.banking.transactions.dto.transaction.CreateInternalTransactionRequest
import org.dbs.banking.transactions.dto.transaction.CreateInternalTransactionResponse
import org.dbs.banking.transactions.dto.transaction.account.GetAccountTransactionCardResponse
import org.dbs.banking.transactions.dto.transaction.account.GetAccountTransactionListResponse
import org.dbs.banking.transactions.dto.transaction.card.GetCardTransactionCardResponse
import org.dbs.banking.transactions.dto.transaction.card.GetCardTransactionListResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.RestHttpConsts.RestQueryParams.AccountParams.QP_ACCOUNT_CODE_MASK
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.QP_PAGE_NUM
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.QP_PAGE_SIZE
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_DECIMAL_TYPE
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_INT_TYPE
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_LONG_TYPE
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_SORT_FIELD
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_SORT_ORDER
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_STRING_TYPE
import org.dbs.consts.RestHttpConsts.RestQueryParams.SWAGGER.QUERY_PAGE_NUM_DEF_VALUE
import org.dbs.consts.RestHttpConsts.RestQueryParams.SWAGGER.QUERY_PAGE_SIZE_DEF_VALUE
import org.dbs.consts.RestHttpConsts.RestQueryParams.SWAGGER.QUERY_PAGE_SIZE_DESC
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_CODE
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_DATE_FROM
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_DATE_TO
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_STATUS_NAME_MASK
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_SUM
import org.dbs.consts.RestHttpConsts.RestQueryParams.TransactionParams.QP_TRANSACTION_TYPE_NAME_MASK
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    RouterOperation(
        path = ROUTE_CREATE_INTERNAL_TRANSACTION,
        method = [POST],
        operation = Operation(
            description =
            """**Create new internal transaction** 
            """,
            tags = [ROUTE_TAG_TRANSACTION],
            operationId = ROUTE_CREATE_INTERNAL_TRANSACTION,
            requestBody = RequestBody(
                description = """Transaction details""",
                content = [Content(schema = Schema(implementation = CreateInternalTransactionRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Create new internal transaction",
                    content = [Content(schema = Schema(implementation = CreateInternalTransactionResponse::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_GET_ACCOUNT_TRANSACTION_LIST,
        method = [GET],
        operation = Operation(
            description = "Get account transaction list",
            tags = [ROUTE_TAG_TRANSACTION],
            operationId = ROUTE_GET_ACCOUNT_TRANSACTION_LIST,
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "Account transaction list",
                content = [Content(schema = Schema(implementation = GetAccountTransactionListResponse::class))]
            )],
            parameters = [Parameter(
                `in` = QUERY,
                name = QP_PAGE_SIZE,
                schema = Schema(type = QP_INT_TYPE),
                required = true,
                description = QUERY_PAGE_SIZE_DESC,
                example = QUERY_PAGE_SIZE_DEF_VALUE
            ), Parameter(
                `in` = QUERY,
                name = QP_PAGE_NUM,
                schema = Schema(type = QP_INT_TYPE),
                description = "query page num",
                example = QUERY_PAGE_NUM_DEF_VALUE
            )]
        )
    ),
    RouterOperation(
        path = ROUTE_GET_ACCOUNT_TRANSACTION_CARD,
        method = [GET],
        operation = Operation(
            description = "Get card account`s transaction",
            tags = [ROUTE_TAG_TRANSACTION],
            operationId = ROUTE_GET_ACCOUNT_TRANSACTION_CARD,
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "Card account`s transaction",
                content = [Content(schema = Schema(implementation = GetAccountTransactionCardResponse::class))]
            )],
            parameters = [Parameter(
                `in` = QUERY,
                name = QP_TRANSACTION_CODE,
                schema = Schema(type = QP_STRING_TYPE),
                description = "Transaction card code",
                example = "BGHBJ"
            )]
        )
    )
)
annotation class SwaggerOpenApiRoutesDefinitions
