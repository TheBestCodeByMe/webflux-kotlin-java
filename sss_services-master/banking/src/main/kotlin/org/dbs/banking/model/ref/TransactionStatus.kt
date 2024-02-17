package org.dbs.banking.model.ref

import org.dbs.ref.serv.enums.TransactionStatusEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("b_transaction_statuses_ref")
data class TransactionStatus(
    @Id
    @Column("status_id")
    val transactionStatus: TransactionStatusEnum,

    @Column("status_name")
    val transactionStatusName: String,

    @Column("status_code")
    val transactionStatusCode: String,

    ) : AbstractRefEntity<Int>() {
    override fun getId() = transactionStatus.getTransactionStatusId()
}
