package org.dbs.banking.repo

import org.dbs.banking.model.AccountTransaction
import org.dbs.banking.repo.sql.CALL_PROCEDURE_INTERNAL_TRANSACTION
import org.dbs.banking.repo.sql.FIND_ACCOUNT_TRANSACTION_CARD_BY_CODE
import org.dbs.banking.transactions.consts.AccountTransactionId
import org.dbs.banking.transactions.consts.TransactionCode
import org.dbs.banking.transactions.consts.TransactionNoteNull
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4CardDb
import org.dbs.consts.AccountId
import org.dbs.consts.CurrencyId
import org.dbs.consts.OperDate
import org.dbs.consts.RestAmount
import org.dbs.consts.TransactionKindId
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Mono

interface AccountTransactionRepo : R2dbcRepository<AccountTransaction, AccountTransactionId> {

    @Query(FIND_ACCOUNT_TRANSACTION_CARD_BY_CODE)
    fun findByCode(@Param("CODE") filterByCode: TransactionCode): Mono<AccountTransaction4CardDb>

    @Query(CALL_PROCEDURE_INTERNAL_TRANSACTION)
    fun callProcedureInternalTransaction(
        @Param("REC_ACC_ID") receiverAccountId: AccountId,
        @Param("SUP_ACC_ID") supplierAccountId: AccountId,
        @Param("DATE") transactionDate: OperDate,
        @Param("AMOUNT") restAmount: RestAmount,
        @Param("REC_TYPE") receiverTransactionTypeId: TransactionKindId,
        @Param("SUP_TYPE") supplierTransactionTypeId: TransactionKindId,
        @Param("REC_TRANSCODE") receiverTransactionCode: TransactionCode,
        @Param("SUP_TRANSCODE") supplierTransactionCode: TransactionCode,
        @Param("NOTE") transactionNote: TransactionNoteNull,
        @Param("CURR_ID") currencyId: CurrencyId,
    ): Mono<Void>
}