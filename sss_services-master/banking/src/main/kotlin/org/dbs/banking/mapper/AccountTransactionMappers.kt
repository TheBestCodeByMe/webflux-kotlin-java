package org.dbs.banking.mapper

import org.dbs.application.core.service.funcs.LocalDateTimeFuncs.toLong
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4CardDb
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4CardDto
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4ListDb
import org.dbs.banking.transactions.dto.transaction.account.AccountTransaction4ListDto
import org.dbs.banking.transactions.dto.transaction.account.AccountTransactionCardDto
import org.dbs.banking.transactions.dto.transaction.account.Recipient4AccountTransactionDto
import org.dbs.banking.transactions.dto.transaction.account.Sender4AccountTransactionDto
import org.dbs.ref.serv.enums.CurrencyEnum
import org.dbs.ref.serv.enums.TransactionKindEnum
import org.dbs.ref.serv.enums.TransactionStatusEnum
import org.dbs.rest.dto.PriceDto
import org.springframework.stereotype.Service

@Service
class AccountTransactionMappers {

    fun map2AccountTransaction4ListDto(src: AccountTransaction4ListDb): AccountTransaction4ListDto = src.run {
        AccountTransaction4ListDto(
            accountCode = account,
            price = PriceDto(transactionSum, CurrencyEnum.getEnum(currencyId).getCurrencyIso()),
            status = TransactionStatusEnum.getEnum(transactionStatusId).getTransactionStatusName(),
            kind = TransactionKindEnum.getEnum(transactionTypeId).getTransactionKindName(),
            transactionCode = transactionNum,
            transactionDate = transactionDate.toLong()
        )
    }

    fun map2AccountTransaction4CardDto(src: AccountTransaction4CardDb, isIncome: Boolean): AccountTransactionCardDto = src.run {
        AccountTransactionCardDto(
            sender = Sender4AccountTransactionDto(
                accountName = if(isIncome) accountNameSec else accountName,
                accountCode = if(isIncome) accountSec else account,
                login = "plug"
            ),
            recipient = Recipient4AccountTransactionDto(
                accountName = if(isIncome) accountName else accountNameSec ,
                accountCode = if(isIncome) account else accountSec,
                login = "plug1"
            ),
            transaction = AccountTransaction4CardDto(
                price = PriceDto(transactionSum, CurrencyEnum.getEnum(currencyId).getCurrencyIso()),
                status = TransactionStatusEnum.getEnum(transactionStatusId).getTransactionStatusName(),
                kind = TransactionKindEnum.getEnum(transactionTypeId).getTransactionKindName(),
                transactionCode = transactionNum,
                transactionDate = transactionDate.toLong(),
                note = transactionNote
            )
        )
    }
}
