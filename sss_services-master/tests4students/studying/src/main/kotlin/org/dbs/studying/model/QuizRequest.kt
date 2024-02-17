package org.dbs.quiz.model

import org.dbs.consts.CustomerId
import org.dbs.consts.OperDate
import org.dbs.entity.core.AbstractEntity
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.QuizRequestId
import org.dbs.quiz.consts.TemplateId
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalTime

@Table("quiz_requests")
data class QuizRequest(
    @Id
    @Column("request_id")
    val quizRequestId: QuizRequestId,
    val requestCode: QuizRequestCode,
    val templateId: TemplateId,
    val timeSpent: LocalTime,
    val openDate: OperDate,
    val deadlineDate: OperDate,
    val startDate: OperDate?,
    val finishDate: OperDate?,
    val remainTime: LocalTime?,
    val custId: CustomerId,
) : AbstractEntity()