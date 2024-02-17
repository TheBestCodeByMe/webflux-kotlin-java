package org.dbs.banking.service

import org.dbs.application.core.service.funcs.TestFuncs.generateTestRangeLong
import org.dbs.application.core.service.funcs.TestFuncs.generateTestString
import org.dbs.banking.dao.AccountDao
import org.dbs.banking.dao.AccountTransactionDao
import org.dbs.banking.mapper.AccountTransactionMappers
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
import org.dbs.ref.serv.enums.TransactionKindEnum.INTERNAL_TRANSACTION
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AccountTransactionService(
    val accountDao: AccountDao,
    val accountTransactionDao: AccountTransactionDao,
    val accountTransactionMappers: AccountTransactionMappers
) : AbstractRestApplicationService() {

    fun generateAccTransactionCode() = generateTestRangeLong(10000, 9999999999).toString() + "-" +
            generateTestString(10) + "-" + generateTestRangeLong(10000, 9999999999).toString()

    fun findByCode(filterByCode: TransactionCode): Mono<AccountTransaction4CardDb> =
        accountTransactionDao.findByCode(filterByCode)

    fun findAllBy(
        filterAccountCodeSearchMask: AccountNum?, filterTransactionSumSearchMask: String?,
        filterTransactionStatusNameSearchMask: TransactionStatusName?,
        filterTransactionTypeNameSearchMask: TransactionKindName?, transactionDateFrom: OperDate,
        transactionDateTo: OperDate, sortField: String, sortOrder: String, pageable: Pageable
    ): Flux<AccountTransaction4ListDb> = accountTransactionDao.findAllBy(
        filterAccountCodeSearchMask, filterTransactionSumSearchMask,
        filterTransactionStatusNameSearchMask, filterTransactionTypeNameSearchMask, transactionDateFrom,
        transactionDateTo, sortField, sortOrder, pageable
    )

    fun countAllBy(
        filterAccountCodeSearchMask: AccountNum?, filterTransactionSumSearchMask: String?,
        filterTransactionStatusNameSearchMask: TransactionStatusName?,
        filterTransactionTypeNameSearchMask: TransactionKindName?, transactionDateFrom: OperDate,
        transactionDateTo: OperDate
    ): Mono<Int> = accountTransactionDao.countAllBy(
        filterAccountCodeSearchMask, filterTransactionSumSearchMask, filterTransactionStatusNameSearchMask,
        filterTransactionTypeNameSearchMask, transactionDateFrom, transactionDateTo
    )

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
    ) = accountTransactionDao.callProcedureInternalTransaction(
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
}
