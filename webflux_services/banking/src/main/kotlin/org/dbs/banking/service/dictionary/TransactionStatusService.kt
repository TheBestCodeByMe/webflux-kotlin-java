package org.dbs.banking.service.dictionary

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.banking.dao.dictionary.TransactionStatusDao
import org.dbs.consts.SpringCoreConst.PropertiesNames.REFERENCES_AUTO_SYNCHRONIZE
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.ref.serv.enums.CountryEnum.entries
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class TransactionStatusService(
    private val transactionStatusDao: TransactionStatusDao
) : AbstractRestApplicationService() {

    @Value("\${$REFERENCES_AUTO_SYNCHRONIZE:$STRING_TRUE}")
    private val autoSynchronize = false

    override fun initialize() = super.initialize().also {
        logger.debug { "Synchronize transaction statuses: $autoSynchronize" }
        if (autoSynchronize) runBlocking {
            launch {
                measureTimeMillis {
                    transactionStatusDao.synchronizeTransactionStatuses()
                }
            }
        }
    }
}
