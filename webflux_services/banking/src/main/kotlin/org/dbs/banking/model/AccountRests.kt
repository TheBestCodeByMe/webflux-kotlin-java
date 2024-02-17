package org.dbs.banking.model

import org.dbs.consts.AccountId
import org.dbs.consts.AccountRestId
import org.dbs.consts.RestAmount
import org.dbs.consts.RestDate
import org.dbs.entity.core.AbstractEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("b_accounts_rests")
data class AccountRests(
    @Id
    @Column("rest_id")
    val restId: AccountRestId,

    @Column("rest_date")
    val restDate: RestDate,

    @Column("account_id")
    val accountId: AccountId,

    @Column("rest_amount")
    val restAmount: RestAmount

) : AbstractEntity()
