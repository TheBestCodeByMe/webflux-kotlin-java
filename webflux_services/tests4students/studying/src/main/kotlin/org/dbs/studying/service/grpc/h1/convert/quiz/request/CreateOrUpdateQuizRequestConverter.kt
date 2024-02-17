package org.dbs.quiz.service.grpc.h1.convert.quiz.request

import org.apache.logging.log4j.kotlin.Logging
import org.dbs.grpc.api.H1h2
import org.dbs.grpc.api.H2H1
import org.dbs.protobuf.quiz.CreateOrUpdateQuizRequestRequest as H2IN
import org.dbs.protobuf.quiz.CreateQuizRequestResponse as H2OUT
import org.dbs.protobuf.quiz.CreatedQuizRequestDto as H2E
import org.dbs.quiz.dto.quiz.request.CreateOrUpdateQuizRequestDto as H1IN
import org.dbs.quiz.dto.quiz.request.CreateQuizRequestResponse as H1OUT
import org.dbs.quiz.dto.quiz.request.CreatedQuizRequest as H1E

@JvmInline
value class CreateOrUpdateQuizRequestConverter(override val entClass: Class<H2E>) :
    H1h2<H1IN, H2IN, H1OUT, H2OUT, H1E, H2E>, Logging {
    override suspend fun buildRequestH2(h1: H1IN): H2IN = h1.run {
        H2IN.newBuilder()
            .setTemplateCode(templateCode)
            .setOpenDate(openDate)
            .also { startDate?.apply { it.setStartDate(this) } }
            .setTimeSpent(timeSpent)
            .also { finishDate?.apply { it.setFinishDate(this) } }
            .also { remainTime?.apply { it.setRemainTime(this) } }
            .setDeadlineDate(deadlineDate)
            .addAllLogins(logins)
            .build()
    }
    override suspend fun buildEntityH1(h2: H2E): H1E =
        h2.run { H1E(templateCode, loginsList) }
    override suspend fun buildResponseH1(): H2H1<H2OUT, H1OUT> = { h2, h1 ->
        h1.also { if (h2.hasResponseAnswer()) h2h1(h2.responseAnswer, h1) }
    }
}
