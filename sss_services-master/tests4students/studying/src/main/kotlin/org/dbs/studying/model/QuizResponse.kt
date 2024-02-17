package org.dbs.quiz.model

import org.dbs.entity.core.AbstractEntity
import org.dbs.quiz.consts.ActualResponses
import org.dbs.quiz.consts.QuestionBody
import org.dbs.quiz.consts.QuizRequestId
import org.dbs.quiz.consts.QuizResponseCode
import org.dbs.quiz.consts.QuizResponseId
import org.dbs.quiz.consts.ValidResponses
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("quiz_responses")
data class QuizResponse(
    @Id
    @Column("response_id")
    val quizResponseId: QuizResponseId,
    val quizRequestId: QuizRequestId,
    val result: Boolean,
    val actualResponses: ActualResponses,
    val questionNum: Int,
    val validResponses: ValidResponses,
    val responseCode: QuizResponseCode,
    val questionBody: QuestionBody
) : AbstractEntity()