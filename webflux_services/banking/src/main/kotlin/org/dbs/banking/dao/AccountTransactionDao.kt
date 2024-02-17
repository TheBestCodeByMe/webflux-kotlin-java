package org.dbs.banking.dao

import org.dbs.banking.repo.AccountTransactionPageableRepo
import org.dbs.banking.repo.AccountTransactionRepo
import org.dbs.banking.transactions.consts.TransactionCode
import org.dbs.banking.transactions.consts.TransactionNoteNull
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4CardDb
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4ListDb
import org.dbs.consts.AccountId
import org.dbs.consts.AccountNum
import org.dbs.consts.CurrencyId
import org.dbs.consts.OperDate
import org.dbs.consts.RestAmount
import org.dbs.consts.TransactionKindId
import org.dbs.consts.TransactionKindName
import org.dbs.consts.TransactionStatusName
import org.dbs.service.R2dbcPersistenceService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AccountTransactionDao internal constructor(
    val accountTransactionRepo: AccountTransactionRepo,
    val accountTransactionPageableRepo: AccountTransactionPageableRepo,
    private val r2dbcPersistenceService: R2dbcPersistenceService,
) : DaoAbstractApplicationService() {


    fun callProcedureInternalTransaction(
        receiverAccountId: AccountId,
        supplierAccountId: AccountId,
        transactionDate: OperDate,
        restAmount: RestAmount,
        receiverTransactionTypeId: TransactionKindId,
        supplierTransactionTypeId: TransactionKindId,
        receiverTransactionCode: TransactionCode,
        supplierTransactionCode: TransactionCode,
        transactionNote: TransactionNoteNull,
        currencyId: CurrencyId
    ) = accountTransactionRepo.callProcedureInternalTransaction(
        receiverAccountId,
        supplierAccountId,
        transactionDate,
        restAmount,
        receiverTransactionTypeId,
        supplierTransactionTypeId,
        receiverTransactionCode,
        supplierTransactionCode,
        transactionNote,
        currencyId
    )

    fun findByCode(filterByCode: TransactionCode): Mono<AccountTransaction4CardDb> =
        accountTransactionRepo.findByCode(filterByCode)

    fun findAllBy(
        filterAccountCodeSearchMask: AccountNum?, filterTransactionSumSearchMask: String?,
        filterTransactionStatusNameSearchMask: TransactionStatusName?,
        filterTransactionTypeNameSearchMask: TransactionKindName?, transactionDateFrom: OperDate,
        transactionDateTo: OperDate, sortField: String, sortOrder: String, pageable: Pageable
    ): Flux<AccountTransaction4ListDb> = accountTransactionPageableRepo.findAllBy(
        filterAccountCodeSearchMask, filterTransactionSumSearchMask,
        filterTransactionStatusNameSearchMask, filterTransactionTypeNameSearchMask, transactionDateFrom,
        transactionDateTo, sortField, sortOrder, pageable
    )

    fun countAllBy(
        filterAccountCodeSearchMask: AccountNum?, filterTransactionSumSearchMask: String?,
        filterTransactionStatusNameSearchMask: TransactionStatusName?,
        filterTransactionTypeNameSearchMask: TransactionKindName?, transactionDateFrom: OperDate,
        transactionDateTo: OperDate
    ): Mono<Int> = accountTransactionPageableRepo.countAllBy(
        filterAccountCodeSearchMask, filterTransactionSumSearchMask, filterTransactionStatusNameSearchMask,
        filterTransactionTypeNameSearchMask, transactionDateFrom, transactionDateTo
    )
}
