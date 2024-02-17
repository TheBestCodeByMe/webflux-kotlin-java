package com.ulia.quiz.testfuncs

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import org.apache.logging.log4j.kotlin.Logging
import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.service.funcs.TestFuncs.generateBool
import org.dbs.application.core.service.funcs.TestFuncs.generateTestRangeInteger
import org.dbs.application.core.service.funcs.TestFuncs.generateTestRangeLong
import org.dbs.application.core.service.funcs.TestFuncs.generateTestString
import org.dbs.application.core.service.funcs.TestFuncs.selectFrom
import org.dbs.consts.Login
import org.dbs.consts.RestHttpConsts.BEARER
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.QP_PAGE_NUM
import org.dbs.consts.RestHttpConsts.RestQueryParams.Pagination.QP_PAGE_SIZE
import org.dbs.consts.RestHttpConsts.RestQueryParams.Quiz.QP_TEMPLATE_CODE
import org.dbs.consts.RestHttpConsts.RestQueryParams.SWAGGER.QUERY_PAGE_NUM_DEF_INT_VALUE
import org.dbs.consts.RestHttpConsts.RestQueryParams.SWAGGER.QUERY_PAGE_SIZE_DEF_INT_VALUE
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginDto
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_CREATE_OR_UPDATE_QUIZ_REQUEST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_CREATE_OR_UPDATE_TEMPLATE
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_GET_TEMPLATE_CARD
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_GET_TEMPLATE_HIST_LIST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_UPDATE_STATUS_QUIZ_REQUEST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_UPDATE_STATUS_START_QUIZ_REQUEST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_UPDATE_STATUS_TEMPLATE
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.MAX_BODY_LENGTH
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_ANS
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_ANS_FAIL
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_ANS_OK
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_CODE
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_DELIMITER
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_NAME
import org.dbs.quiz.consts.QuizConsts.Routes.TemplateBody.QT_QUESTION
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.TemplateCode
import org.dbs.quiz.consts.TemplateCodeNull
import org.dbs.quiz.consts.TemplateName
import org.dbs.quiz.dao.TemplateDao
import org.dbs.quiz.dto.quiz.request.CreateOrUpdateQuizRequestDto
import org.dbs.quiz.dto.quiz.request.CreateOrUpdateQuizRequestRequest
import org.dbs.quiz.dto.quiz.request.CreateQuizRequestResponse
import org.dbs.quiz.dto.quiz.request.UpdateStatusQuizRequestDto
import org.dbs.quiz.dto.quiz.request.UpdateStatusQuizRequestRequest
import org.dbs.quiz.dto.quiz.request.UpdateStatusQuizRequestResponse
import org.dbs.quiz.dto.quiz.request.UpdateStatusStartQuizRequestDto
import org.dbs.quiz.dto.quiz.request.UpdateStatusStartQuizRequestRequest
import org.dbs.quiz.dto.quiz.request.UpdateStatusStartQuizRequestResponse
import org.dbs.quiz.dto.template.CreateOrUpdateTemplateDto
import org.dbs.quiz.dto.template.CreateOrUpdateTemplateRequest
import org.dbs.quiz.dto.template.CreateTemplateResponse
import org.dbs.quiz.dto.template.GetTemplateDtoResponse
import org.dbs.quiz.dto.template.GetTemplateHistListResponse
import org.dbs.quiz.dto.template.QuizTemplateHistListDto
import org.dbs.quiz.dto.template.UpdateStatusTemplateDto
import org.dbs.quiz.dto.template.UpdateStatusTemplateRequest
import org.dbs.quiz.dto.template.UpdateStatusTemplateResponse
import org.dbs.quiz.enums.TemplateGroupEnum.Companion.templateGroupCodes
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.model.Template
import org.dbs.quiz.service.TemplateService
import org.dbs.test.ko.WebTestClientFuncs.executeGetRequestV2
import org.dbs.test.ko.WebTestClientFuncs.executePostRequestV2
import org.springframework.http.HttpHeaders.AUTHORIZATION
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

object QuizRequestFuncs : Logging {

    private fun TemplateDao.quizRequestCount() = templateRepo.count()
    fun TemplateService.quizRequestCount() = templateDao.quizRequestCount()
    fun AbstractQuizTest.quizRequestCount() = templateService.quizRequestCount()

    suspend fun AbstractQuizTest.createTestQuizRequest(
        templateCode: TemplateCode,
        logins: Collection<Login>,
        timeSpent: Long = generateTime(),
        openDate: Long = generateTime(),
        deadlineDate: Long = generateTime(),
        startDate: Long = generateTime(),
        finishDate: Long = generateTime(),
        remainTime: Long = generateTime(),
    ) = createOrUpdateTestQuizRequest(
        templateCode,
        logins,
        timeSpent,
        openDate,
        deadlineDate,
        startDate,
        finishDate,
        remainTime
    )

    suspend fun AbstractQuizTest.createOrUpdateTestQuizRequest(
        templateCode: TemplateCode,
        logins: Collection<Login>,
        timeSpent: Long = generateTime(),
        openDate: Long = generateTime(),
        deadlineDate: Long = generateTime(),
        startDate: Long = generateTime(),
        finishDate: Long = generateTime(),
        remainTime: Long = generateTime(),
    ): Mono<Collection<QuizRequest>> = run {

        runTest {
            val testedRoute = ROUTE_CREATE_OR_UPDATE_QUIZ_REQUEST
            logger.info("execute route '$testedRoute'")

            val dto = CreateOrUpdateQuizRequestDto(
                templateCode = templateCode,
                logins = logins,
                timeSpent = timeSpent,
                openDate = openDate,
                deadlineDate = deadlineDate,
                startDate = startDate,
                finishDate = finishDate,
                remainTime = remainTime
            )

            executePostRequestV2(
                testedRoute,
                CreateOrUpdateQuizRequestRequest(requestBodyDto = dto),
                CreateQuizRequestResponse::class.java,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                it.templateCode.shouldBeEqual(templateCode)

                quizRequestService.quizRequestDao.findByTemplateCode(dto.templateCode)
                    .collectList()
                    .map { createdQuizTemplate ->
                        logger.debug { "compare QuizTemplate attrs (${dto.templateCode})" }
                        createdQuizTemplate.size.shouldBeEqual(logins.size)
                        createdQuizTemplate
                    }
            }
        }
    }

    suspend fun AbstractQuizTest.updateStatusTestQuizRequest(
        quizRequestCode: QuizRequestCode,
        newStatus: String,
    ): Mono<QuizRequest> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_STATUS_QUIZ_REQUEST
            logger.info("execute route '$testedRoute'")

            val dto = UpdateStatusQuizRequestDto(
                quizRequestCode = quizRequestCode,
                newStatus = newStatus
            )

            executePostRequestV2(
                testedRoute,
                UpdateStatusQuizRequestRequest(requestBodyDto = dto),
                UpdateStatusQuizRequestResponse::class.java,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                quizRequestService.quizRequestDao.findByQuizRequestCode(dto.quizRequestCode)
                    .switchIfEmpty { error("${dto.quizRequestCode}: unknown QuizRequest") }
                    .map { createdQuizRequest ->
                        createdQuizRequest.coreEntity.entityStatus.entityStatusShortName.shouldBeEqual(newStatus)
                        logger.debug { "compare QuizRequest attrs (${dto.quizRequestCode})" }
                        createdQuizRequest
                    }
            }
        }
    }

    suspend fun AbstractQuizTest.updateStatusTestStartQuizRequest(
        quizRequestCode: QuizRequestCode,
        newStatus: String,
    ): Mono<QuizRequest> = run {

        runTest {
            val testedRoute = ROUTE_UPDATE_STATUS_START_QUIZ_REQUEST
            logger.info("execute route '$testedRoute'")

            val dto = UpdateStatusStartQuizRequestDto(
                quizRequestCode = quizRequestCode,
                newStatus = newStatus
            )

            executePostRequestV2(
                testedRoute,
                UpdateStatusStartQuizRequestRequest(requestBodyDto = dto),
                UpdateStatusStartQuizRequestResponse::class.java,
                { it.add(AUTHORIZATION, BEARER + managerJwt) }
            )
            {
                dto.shouldNotBeNull()

                quizRequestService.quizRequestDao.findByQuizRequestCode(dto.quizRequestCode)
                    .switchIfEmpty { error("${dto.quizRequestCode}: unknown QuizRequest") }
                    .map { createdQuizRequest ->
                        createdQuizRequest.coreEntity.entityStatus.entityStatusShortName.shouldBeEqual(newStatus)
                        logger.debug { "compare QuizRequest attrs (${dto.quizRequestCode})" }
                        createdQuizRequest
                    }
            }
        }
    }

    private fun generateTime() = generateTestRangeLong(1000, 1000000)
}
