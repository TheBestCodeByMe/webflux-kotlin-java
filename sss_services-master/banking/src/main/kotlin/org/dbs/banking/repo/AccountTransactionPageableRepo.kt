package org.dbs.banking.repo

import org.dbs.banking.model.AccountTransaction
import org.dbs.banking.repo.sql.SELECT_ACCOUNT_FIELDS
import org.dbs.banking.repo.sql.SELECT_COUNT_ACCOUNT_TRANSACTIONS
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4ListDb
import org.dbs.consts.AccountNum
import org.dbs.consts.EntityId
import org.dbs.consts.OperDate
import org.dbs.consts.TransactionKindName
import org.dbs.consts.TransactionStatusName
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccountTransactionPageableRepo : ReactiveCrudRepository<AccountTransaction, EntityId> {

    @Query(SELECT_COUNT_ACCOUNT_TRANSACTIONS)
    fun countAllBy(
        @Param("ACCOUNT") filterAccountCodeSearchMask: AccountNum?,
        @Param("SUM") filterTransactionSumSearchMask: String?,
        @Param("STATUS") filterTransactionStatusNameSearchMask: TransactionStatusName?,
        @Param("KIND") filterTransactionTypeNameSearchMask: TransactionKindName?,
        @Param("DATE_FROM") transactionDateFrom: OperDate,
        @Param("DATE_TO") transactionDateTo: OperDate,
    ): Mono<Int>

    @Query(SELECT_ACCOUNT_FIELDS)
    fun findAllBy(
        @Param("ACCOUNT") filterAccountCodeSearchMask: AccountNum?,
        @Param("SUM") filterTransactionSumSearchMask: String?,
        @Param("STATUS") filterTransactionStatusNameSearchMask: TransactionStatusName?,
        @Param("KIND") filterTransactionTypeNameSearchMask: TransactionKindName?,
        @Param("DATE_FROM") transactionDateFrom: OperDate,
        @Param("DATE_TO") transactionDateTo: OperDate,
        sortField: String, sortOrder: String,
        pageable: Pageable
    ): Flux<AccountTransaction4ListDb>
}
