package org.dbs.quiz.service

import org.dbs.application.core.service.funcs.TestFuncs.generateTestString
import org.dbs.consts.CustomerId
import org.dbs.consts.OperDate
import org.dbs.consts.SysConst.LOCALTIME_BASE
import org.dbs.consts.SysConst.OPERDATE_BASE
import org.dbs.customers.consts.CustomerLogin
import org.dbs.entity.core.AbstractEntityExtension.createCoreEntity
import org.dbs.entity.core.AbstractEntityExtension.updateStatus
import org.dbs.entity.core.enums.EntityStatusEnum
import org.dbs.entity.core.enums.EntityStatusEnum.S3_QUIZ_REQUEST_STATUS_ACTUAL
import org.dbs.entity.core.enums.EntityStatusEnum.S3_QUIZ_REQUEST_STATUS_CLOSED
import org.dbs.entity.core.enums.EntityStatusEnum.S3_QUIZ_REQUEST_STATUS_FINISHED
import org.dbs.entity.core.enums.EntityStatusEnum.S3_QUIZ_REQUEST_STATUS_IN_PROGRESS
import org.dbs.entity.core.enums.EntityTypeEnum.S3_QUIZ_REQUEST
import org.dbs.entity.core.enums.EntityTypeEnumExtension.registerAllowedStatusesChanges
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.TemplateCode
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.dao.QuizRequestDao
import org.dbs.quiz.dto.quiz.response.QuizRequestInfoDb
import org.dbs.quiz.mapper.QuizRequestMappers
import org.dbs.quiz.model.QuizRequest
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.dbs.service.R2dbcPersistenceService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Lazy(false)
@Service
class QuizRequestService(
    val r2dbcPersistenceService: R2dbcPersistenceService,
    val quizRequestDao: QuizRequestDao,
    val quizRequestMappers: QuizRequestMappers,
) : AbstractRestApplicationService() {

    fun findByQuizRequestCode(quizRequestCode: QuizRequestCode):
            Mono<QuizRequest> = quizRequestDao.findByQuizRequestCode(quizRequestCode)

    fun findByTemplateCode(templateCode: TemplateCode):
            Flux<QuizRequest> = quizRequestDao.findByTemplateCode(templateCode)

    fun findByCustomerIdsAndTemplateId(customerIds: Collection<CustomerId>, templateId: TemplateId):
            Flux<QuizRequest> = quizRequestDao.findByCustomerIdsAndTemplateId(customerIds, templateId)

    fun findInfo4QuizResponse(quizRequestCode: QuizRequestCode):
            Mono<QuizRequestInfoDb> = quizRequestDao.findInfo4QuizResponse(quizRequestCode)

    fun createNewQuizRequest(
        quizRequestCode: QuizRequestCode,
        templateId: TemplateId,
        customerId: CustomerId
    ): Mono<QuizRequest> =
        generateNewEntityId(QuizRequest::class.java)
            .map { newEntityId ->
                logger.debug { "create new quiz request code: $quizRequestCode (entityId=$newEntityId)" }
                QuizRequest(
                    quizRequestId = newEntityId,
                    templateId = templateId,
                    custId = customerId,
                    requestCode = quizRequestCode,
                    openDate = OPERDATE_BASE,
                    startDate = OPERDATE_BASE,
                    finishDate = OPERDATE_BASE,
                    deadlineDate = OPERDATE_BASE,
                    timeSpent = LOCALTIME_BASE,
                    remainTime = LOCALTIME_BASE
                ).let {
                    it.createCoreEntity(
                        newEntityId,
                        S3_QUIZ_REQUEST,
                        S3_QUIZ_REQUEST_STATUS_ACTUAL
                    )
                    it
                }
            }

    fun generateQuizRequestCode(customerLogin: CustomerLogin, openDate: OperDate, templateCode: TemplateCode) =
        customerLogin.lowercase(Locale.getDefault()) + "-" + templateCode.lowercase(Locale.getDefault()) +
                "-" + openDate.dayOfYear + openDate.year + generateTestString(10)

    fun setQuizRequestNewStatus(quizRequest: QuizRequest, newEntityStatusEnum: EntityStatusEnum) = quizRequest.run {
        updateStatus(
            newEntityStatusEnum, newEntityStatusEnum == S3_QUIZ_REQUEST_STATUS_CLOSED
        )
        quizRequestDao.invalidateCaches(quizRequest.requestCode)
        toMono()
    }

    companion object {
        val allowedQuizRequestStatuses by lazy {
            listOf(
                S3_QUIZ_REQUEST_STATUS_ACTUAL,
                S3_QUIZ_REQUEST_STATUS_IN_PROGRESS,
                S3_QUIZ_REQUEST_STATUS_FINISHED,
                S3_QUIZ_REQUEST_STATUS_CLOSED
            )
        }

        val allowedShortQuizRequestStatuses by lazy {
            allowedQuizRequestStatuses.map { it.entityStatusShortName }
        }

        val allowedStatuses4StartingQuizRequest by lazy {
            listOf(
                S3_QUIZ_REQUEST_STATUS_IN_PROGRESS,
            )
        }

        val allowedShortStatuses4StartingQuizRequest by lazy {
            allowedStatuses4StartingQuizRequest.map { it.entityStatusShortName }
        }
    }

    override fun initialize() = super.initialize().also {
        // experimental future: allowed statuses updated (from -> to)
        S3_QUIZ_REQUEST.registerAllowedStatusesChanges(
            mapOf(     // statuses  "to" (listOf)
                S3_QUIZ_REQUEST_STATUS_ACTUAL to listOf(
                    S3_QUIZ_REQUEST_STATUS_CLOSED,
                    S3_QUIZ_REQUEST_STATUS_IN_PROGRESS,
                ),
                S3_QUIZ_REQUEST_STATUS_IN_PROGRESS to listOf(
                    S3_QUIZ_REQUEST_STATUS_FINISHED,
                    S3_QUIZ_REQUEST_STATUS_CLOSED,
                ),
                S3_QUIZ_REQUEST_STATUS_FINISHED to listOf(
                    S3_QUIZ_REQUEST_STATUS_CLOSED,
                )
            )
        )
    }
}
