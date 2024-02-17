package org.dbs.banking.dao.dictionary

import org.dbs.banking.model.ref.TransactionStatus
import org.dbs.banking.repo.dictionary.TransactionStatusRepo
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.ref.serv.enums.TransactionStatusEnum.Companion.isExistEnum
import org.dbs.ref.serv.enums.TransactionStatusEnum.entries
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux.fromIterable
import kotlin.system.measureTimeMillis

@Service
@Lazy(false)
class TransactionStatusDao(private val transactionStatusRepo: TransactionStatusRepo) : DaoAbstractApplicationService() {
    suspend fun synchronizeTransactionStatuses() = measureTimeMillis {
        fromIterable(statuses)
            .publishOn(parallelScheduler)
            .noDuplicates({ it }, { it.transactionStatusName }, { it.transactionStatusCode })
            .synchronizeReference(transactionStatusRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })
        transactionStatusRepo.findAll().validateDb { isExistEnum(it.id) }.count().subscribeMono()
    }

    val statuses by lazy {
        entries.map { TransactionStatus(it, it.getTransactionStatusName(), it.getTransactionStatusCode()) }
    }
}
