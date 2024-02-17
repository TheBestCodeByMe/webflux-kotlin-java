package org.dbs.banking.repo.dictionary

import org.dbs.banking.model.ref.TransactionKind
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface TransactionKindRepo : R2dbcRepository<TransactionKind, Int>
