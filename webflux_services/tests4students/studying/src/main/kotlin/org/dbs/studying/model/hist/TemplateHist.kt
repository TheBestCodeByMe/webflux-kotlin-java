package org.dbs.quiz.model.hist

import org.dbs.consts.OperDate
import org.dbs.entity.core.AbstractEntity
import org.dbs.quiz.consts.TemplateCode
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.consts.TemplateName
import org.dbs.quiz.enums.TemplateGroupEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("quiz_templates_hist")
data class TemplateHist(
    @Id
    @Column("template_id")
    val templateId: TemplateId,
    @Column("actual_date")
    val actualDate: OperDate,
    val templateCode: TemplateCode,
    val name: TemplateName,
    @Column("group_id")
    val group: TemplateGroupEnum,
    val body: String,
    @Column("quiz_timeout")
    val timeout: Int,
    @Column("body_hash")
    val hash: Int,
) : AbstractEntity()
