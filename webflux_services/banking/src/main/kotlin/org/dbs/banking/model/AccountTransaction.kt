package org.dbs.banking.model

import org.dbs.banking.transactions.consts.AccountTransactionId
import org.dbs.banking.transactions.consts.CardTransactionIdNull
import org.dbs.banking.transactions.consts.TransactionCode
import org.dbs.banking.transactions.consts.TransactionNoteNull
import org.dbs.consts.AccountId
import org.dbs.consts.Money
import org.dbs.consts.OperDate
import org.dbs.entity.core.AbstractEntity
import org.dbs.ref.serv.enums.CurrencyEnum
import org.dbs.ref.serv.enums.TransactionKindEnum
import org.dbs.ref.serv.enums.TransactionStatusEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("b_accounts_transactions")
data class AccountTransaction(
    @Id
    @Column("transaction_id")
    val accountTransactionId: AccountTransactionId,

    @Column("account_id")
    val accountId: AccountId,

    @Column("transaction_date")
    val transactionDate: OperDate,

    @Column("transaction_sum")
    val transactionSum: Money,

    @Column("transaction_type_id")
    val transactionType: TransactionKindEnum,

    @Column("linked_transaction_id")
    val linkedTransactionId: AccountTransactionId,

    @Column("card_transaction_id")
    val cardTransactionId: CardTransactionIdNull,

    @Column("transaction_note")
    val transactionNote: TransactionNoteNull,

    @Column("currency_id")
    val currency: CurrencyEnum,

    @Column("transaction_code")
    val transactionCode: TransactionCode,

    @Column("transaction_status_id")
    val transactionStatus: TransactionStatusEnum,

    ) : AbstractEntity()
