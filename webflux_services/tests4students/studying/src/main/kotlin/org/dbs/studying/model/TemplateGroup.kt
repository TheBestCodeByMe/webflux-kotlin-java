package org.dbs.quiz.model

import org.dbs.consts.EntityCode
import org.dbs.consts.ReferenceId
import org.dbs.consts.ReferenceName
import org.dbs.quiz.enums.TemplateGroupEnum
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("quiz_groups_ref")
data class TemplateGroup(
    @Id
    @Column("group_id")
    val groupId: TemplateGroupEnum,
    @Column("group_code")
    val groupCode: EntityCode,
    @Column("group_name")
    val groupName: ReferenceName,

    ) : AbstractRefEntity<ReferenceId>() {
    override fun getId() = groupId.getCode()
}
