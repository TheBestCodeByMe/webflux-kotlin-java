package org.dbs.banking.repo

import org.dbs.banking.model.AccountRests
import org.dbs.consts.AccountRestId
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface AccountRestsRepo : R2dbcRepository<AccountRests, AccountRestId> {
}
