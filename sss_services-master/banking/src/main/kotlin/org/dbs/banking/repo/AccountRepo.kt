package org.dbs.banking.repo

import org.dbs.banking.model.Account
import org.dbs.banking.repo.sql.SELECT_ACCOUNT_BY_NUM_STATUS
import org.dbs.banking.repo.sql.SELECT_ACCOUNT_BY_NUM_STATUS_SUM
import org.dbs.consts.AccountId
import org.dbs.consts.AccountNum
import org.dbs.consts.EntityStatusId
import org.dbs.consts.Money
import org.dbs.consts.RestDate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface AccountRepo : R2dbcRepository<Account, AccountId> {
    @Query(SELECT_ACCOUNT_BY_NUM_STATUS_SUM)
    fun findAccountByAccountAndStatusAndSum(
        account: AccountNum,
        entityStatusId: EntityStatusId?,
        sum: Money?,
        restDate: RestDate
    ): Mono<Account>

    @Query(SELECT_ACCOUNT_BY_NUM_STATUS)
    fun findAccountByAccountAndStatus(
        account: AccountNum,
        entityStatusId: EntityStatusId?,
    ): Mono<Account>
}
