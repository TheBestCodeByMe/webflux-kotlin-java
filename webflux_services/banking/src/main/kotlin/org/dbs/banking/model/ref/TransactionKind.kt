package org.dbs.banking.model.ref

import org.dbs.ref.serv.enums.TransactionKindEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("b_transaction_kinds_ref")
data class TransactionKind(
    @Id
    @Column("kind_id")
    val transactionKind: TransactionKindEnum,

    @Column("kind_name")
    val transactionKindName: String,

    @Column("kind_code")
    val transactionKindCode: String,

) : AbstractRefEntity<Int>() {
    override fun getId() = transactionKind.getTransactionKindId()
}
