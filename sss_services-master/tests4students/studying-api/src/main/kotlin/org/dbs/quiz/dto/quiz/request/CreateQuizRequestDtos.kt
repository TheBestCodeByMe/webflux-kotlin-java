package org.dbs.quiz.dto.quiz.request

import org.dbs.consts.Login
import org.dbs.consts.OperDateDto
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.quiz.consts.TemplateCode
import org.dbs.rest.api.consts.RequestId
import org.dbs.rest.api.nio.AbstractHttpRequestBody
import org.dbs.rest.api.nio.HttpResponseBody
import org.dbs.rest.api.nio.RequestDto
import org.dbs.rest.api.nio.ResponseDto

data class CreateOrUpdateQuizRequestDto(
    val templateCode: TemplateCode,
    val logins: Collection<Login>,
    val timeSpent: OperDateDto,
    val openDate: OperDateDto,
    val deadlineDate: OperDateDto,
    val startDate: OperDateDto?,
    val finishDate: OperDateDto?,
    val remainTime: OperDateDto?,
) : RequestDto

data class CreatedQuizRequest(
    val templateCode: TemplateCode,
    val logins: Collection<Login>,
) : ResponseDto

data class CreateOrUpdateQuizRequestRequest(
    override val requestBodyDto: CreateOrUpdateQuizRequestDto
) : AbstractHttpRequestBody<CreateOrUpdateQuizRequestDto>()

data class CreateQuizRequestResponse(
    private val httpRequestId: RequestId = EMPTY_STRING
) : HttpResponseBody<CreatedQuizRequest>(httpRequestId)