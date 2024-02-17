package org.dbs.banking.repo.dictionary

import org.dbs.banking.model.ref.TransactionStatus
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface TransactionStatusRepo : R2dbcRepository<TransactionStatus, Int>
