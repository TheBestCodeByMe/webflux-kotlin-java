package org.dbs.quiz.dao

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.consts.CustomerId
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.TemplateCode
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.dto.quiz.response.QuizRequestInfoDb
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.repo.QuizRequestRepo
import org.dbs.service.R2dbcPersistenceService
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_QUIZ_REQUEST_CODE_2_ENTITY
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_QUIZ_REQUEST_CODE_2_ENTITY_ID
import org.dbs.service.cache.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class QuizRequestDao(
    private val r2dbcPersistenceService: R2dbcPersistenceService,
    private val entityCacheService: EntityCacheService<QuizRequest>,
    val quizRequestRepo: QuizRequestRepo,
) : DaoAbstractApplicationService() {

    fun findByQuizRequestCode(quizRequestCode: QuizRequestCode):
            Mono<QuizRequest> = quizRequestRepo.findByRequestCode(quizRequestCode)
        .flatMap { r2dbcPersistenceService.initCoreEntity(it, it.quizRequestId) }

    fun findByTemplateCode(templateCode: TemplateCode):
            Flux<QuizRequest> = quizRequestRepo.findByTemplateCode(templateCode)

    fun findByCustomerIdsAndTemplateId(customerIds: Collection<CustomerId>, templateId: TemplateId):
            Flux<QuizRequest> = quizRequestRepo.findByCustomerIdsAndTemplateId(customerIds, templateId)
        .collectList().flatMapMany { r2dbcPersistenceService.initCoreEntities(it, QuizRequest::quizRequestId) }

    fun findInfo4QuizResponse(quizRequestCode: QuizRequestCode):
            Mono<QuizRequestInfoDb> = quizRequestRepo.findInfo4QuizResponse(quizRequestCode)

    fun invalidateCaches(code: String) {
        runBlocking {
            launch {
                entityCacheService.invalidateCaches(
                    code,
                    CACHE_QUIZ_REQUEST_CODE_2_ENTITY,
                    CACHE_QUIZ_REQUEST_CODE_2_ENTITY_ID
                )
            }
        }
    }
}
