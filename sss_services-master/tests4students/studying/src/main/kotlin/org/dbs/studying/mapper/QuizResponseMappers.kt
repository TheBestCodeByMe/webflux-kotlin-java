package org.dbs.quiz.mapper

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.entity.core.AbstractEntityExtension.assignCoreEntity
import org.dbs.protobuf.quiz.CreateOrUpdateQuizRequestRequest
import org.dbs.protobuf.quiz.CreateOrUpdateQuizResponseRequest
import org.dbs.quiz.consts.ValidResponses
import org.dbs.quiz.dto.quiz.response.QuizRequestInfoDb
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.model.QuizResponse
import org.springframework.stereotype.Service

@Service
class QuizResponseMappers : Logging {

    fun updateQuizResponse(
        src: QuizResponse,
        srcDto: CreateOrUpdateQuizResponseRequest,
        quizRequestInfoDb: QuizRequestInfoDb,
    ): QuizResponse =
        src.copy(
            result = true, // TODO: change on result
            questionNum = src.questionNum,
            actualResponses = src.actualResponses,
            validResponses = "validResponses", // TODO: change on validResponses
            questionBody = src.questionBody
        ).also { it.assignCoreEntity(src) }

}