package org.dbs.banking.dao


import org.dbs.banking.repo.AccountRestsRepo
import org.dbs.service.R2dbcPersistenceService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service

@Service
class AccountRestsDao internal constructor(
    val accountRestsRepo: AccountRestsRepo,
    private val r2dbcPersistenceService: R2dbcPersistenceService,
) : DaoAbstractApplicationService() {
}
