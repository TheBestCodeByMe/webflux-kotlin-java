package org.dbs.banking.config

import org.dbs.ref.serv.enums.convert.CurrencyConverter
import org.dbs.ref.serv.enums.convert.CurrencyEnumConverter
import org.dbs.ref.serv.enums.convert.TransactionKindConverter
import org.dbs.ref.serv.enums.convert.TransactionKindEnumConverter
import org.dbs.ref.serv.enums.convert.TransactionStatusConverter
import org.dbs.ref.serv.enums.convert.TransactionStatusEnumConverter
import org.dbs.service.GenericEntityR2dbcConfiguration
import org.springframework.stereotype.Service

@Service
class PgR2dbcConfiguration : GenericEntityR2dbcConfiguration() {
    override fun addExtraCustomConverters(converters: MutableCollection<Any>) {
        with(converters) {
            super.addExtraCustomConverters(this)
            add(TransactionStatusEnumConverter())
            add(TransactionStatusConverter())
            add(TransactionKindEnumConverter())
            add(TransactionKindConverter())
            add(CurrencyEnumConverter())
            add(CurrencyConverter())
        }
    }
}
