package org.dbs.quiz.dto.quiz.response

import org.dbs.consts.CustomerId
import org.dbs.consts.OperDate
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.quiz.consts.ActualResponses
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.QuizRequestId
import org.dbs.quiz.consts.QuizResponseCode
import org.dbs.quiz.consts.Responses
import org.dbs.quiz.consts.TemplateId
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto
import java.time.LocalTime

data class QuizRequestInfoDb (
    val requestId: QuizRequestId,
    val requestCode: QuizRequestCode,
    val templateId: TemplateId,
    val timeSpent: LocalTime,
    val openDate: OperDate,
    val deadlineDate: OperDate,
    val startDate: OperDate?,
    val finishDate: OperDate?,
    val remainTime: LocalTime?,
    val custId: CustomerId,
    val body: String,
)

data class QuizResponseDto (
    val questionNum: Int,
    val questionWithAnswers: String,
    val actualResponses: Collection<ActualResponses>,
) : RequestDto

data class CreateOrUpdateQuizResponseDto (
    val quizRequestCode: String,
    val quizResponses: Collection<QuizResponseDto>,
) : RequestDto

data class CreatedQuizResponseDto(
    val quizResponseCode: QuizResponseCode,
) : ResponseDto

data class CreateOrUpdateQuizResponseRequest(
    override val requestBodyDto: CreateOrUpdateQuizResponseDto
) : AbstractHttpRequestBody<CreateOrUpdateQuizResponseDto>()

data class CreateQuizResponseResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedQuizResponseDto>(httpRequestId)