package org.dbs.banking.dao


import org.dbs.banking.repo.AccountRepo
import org.dbs.consts.AccountNum
import org.dbs.consts.EntityStatusId
import org.dbs.consts.Money
import org.dbs.consts.RestDate
import org.dbs.service.R2dbcPersistenceService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service

@Service
class AccountDao internal constructor(
    val accountRepo: AccountRepo,
    private val r2dbcPersistenceService: R2dbcPersistenceService,
) : DaoAbstractApplicationService() {

    fun findAccount(account: AccountNum, entityStatusId: EntityStatusId?, sum: Money?, restDate: RestDate) =
        accountRepo.findAccountByAccountAndStatusAndSum(account, entityStatusId, sum, restDate)

    fun findAccount(account: AccountNum, entityStatusId: EntityStatusId) =
        accountRepo.findAccountByAccountAndStatus(account, entityStatusId)
}
