package org.dbs.quiz.mapper

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.entity.core.AbstractEntityExtension.assignCoreEntity
import org.dbs.protobuf.quiz.CreateOrUpdateQuizRequestRequest
import org.dbs.quiz.model.QuizRequest
import org.springframework.stereotype.Service

@Service
class QuizRequestMappers : Logging {

    fun updateQuizRequest(src: QuizRequest, srcDto: CreateOrUpdateQuizRequestRequest): QuizRequest =
        src.copy(
            openDate = srcDto.openDate.toLocalDateTime(),
            finishDate = srcDto.finishDate.toLocalDateTime(),
            startDate = srcDto.startDate.toLocalDateTime(),
            timeSpent = srcDto.timeSpent.toLocalDateTime().toLocalTime(),
            remainTime = srcDto.remainTime.toLocalDateTime().toLocalTime(),
            deadlineDate = srcDto.deadlineDate.toLocalDateTime(),
        ).also { it.assignCoreEntity(src) }

}
