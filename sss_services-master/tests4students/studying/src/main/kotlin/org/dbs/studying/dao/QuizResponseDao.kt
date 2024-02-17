package org.dbs.quiz.dao

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.consts.CustomerId
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.TemplateCode
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.model.QuizResponse
import org.dbs.quiz.repo.QuizRequestRepo
import org.dbs.quiz.repo.QuizResponseRepo
import org.dbs.service.R2dbcPersistenceService
import org.dbs.service.cache.EntityCacheKeyEnum
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_QUIZ_REQUEST_CODE_2_ENTITY
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_QUIZ_REQUEST_CODE_2_ENTITY_ID
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_QUIZ_RESPONSE_CODE_2_ENTITY
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_QUIZ_RESPONSE_CODE_2_ENTITY_ID
import org.dbs.service.cache.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class QuizResponseDao(
    private val r2dbcPersistenceService: R2dbcPersistenceService,
    private val entityCacheService: EntityCacheService<QuizResponse>,
    val quizResponseRepo: QuizResponseRepo,
) : DaoAbstractApplicationService() {

    fun findByRequestCode(quizRequestCode: QuizRequestCode):
            Flux<QuizResponse> = quizResponseRepo.findByRequestCode(quizRequestCode)
        .collectList().flatMapMany { r2dbcPersistenceService.initCoreEntities(it, QuizResponse::quizResponseId) }

    fun invalidateCaches(code: String) {
        runBlocking {
            launch {
                entityCacheService.invalidateCaches(
                    code,
                    CACHE_QUIZ_RESPONSE_CODE_2_ENTITY,
                    CACHE_QUIZ_RESPONSE_CODE_2_ENTITY_ID
                )
            }
        }
    }
}
