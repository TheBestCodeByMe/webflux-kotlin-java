package org.dbs.banking.model

import org.dbs.consts.AccountId
import org.dbs.consts.AccountName
import org.dbs.consts.AccountNum
import org.dbs.consts.CustomerId
import org.dbs.entity.core.AbstractEntity
import org.dbs.ref.serv.enums.CurrencyEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("b_accounts")
data class Account(
    @Id
    @Column("account_id")
    val accountId: AccountId,

    @Column("customer_id")
    val customerId: CustomerId,

    @Column("account")
    val account: AccountNum,

    @Column("account_name")
    val accountName: AccountName,

    @Column("currency_id")
    val currency: CurrencyEnum
) : AbstractEntity()
