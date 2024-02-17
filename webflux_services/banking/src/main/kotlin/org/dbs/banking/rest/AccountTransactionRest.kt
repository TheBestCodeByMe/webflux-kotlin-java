package org.dbs.banking.rest

import org.dbs.banking.service.AccountTransactionService
import org.dbs.banking.value.CreateInternalTransactionValueRequest
import org.dbs.banking.value.transaction.account.GetCardAccountsTransactionValueRequest
import org.dbs.banking.value.transaction.account.GetListAccountTransactionValueRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequest
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
class AccountTransactionRest(
    private val accountTransactionService: AccountTransactionService,
) : H1_PROCESSOR<ServerRequest>() {
    fun createInternalTransaction(serverRequest: ServerRequest) = serverRequest.doRequest {
        CreateInternalTransactionValueRequest(it).buildResponse(
            this@AccountTransactionRest,
            accountTransactionService
        )
    }

    fun getAccountTransactionList(serverRequest: ServerRequest) = serverRequest.doRequest {
        GetListAccountTransactionValueRequest(it).buildResponse(
            this@AccountTransactionRest,
            accountTransactionService
        )
    }

    fun getAccountTransactionCard(serverRequest: ServerRequest) = serverRequest.doRequest {
        GetCardAccountsTransactionValueRequest(it).buildResponse(
            this@AccountTransactionRest,
            accountTransactionService
        )
    }
}
