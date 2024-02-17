package org.dbs.banking.dao.dictionary

import org.dbs.banking.model.ref.TransactionKind
import org.dbs.banking.repo.dictionary.TransactionKindRepo
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.ref.serv.enums.TransactionKindEnum.Companion.isExistEnum
import org.dbs.ref.serv.enums.TransactionKindEnum.entries
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux.fromIterable
import kotlin.system.measureTimeMillis

@Service
@Lazy(false)
class TransactionKindDao(private val transactionKindRepo: TransactionKindRepo) : DaoAbstractApplicationService() {
    suspend fun synchronizeTransactionKinds() = measureTimeMillis {
        fromIterable(statuses)
            .publishOn(parallelScheduler)
            .noDuplicates({ it }, { it.transactionKindName }, { it.transactionKindCode })
            .synchronizeReference(transactionKindRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })
        transactionKindRepo.findAll().validateDb { isExistEnum(it.id) }.count().subscribeMono()
    }

    val statuses by lazy {
        entries.map { TransactionKind(it, it.getTransactionKindName(), it.getTransactionKindCode()) }
    }
}
