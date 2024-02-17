package org.dbs.quiz.dao

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.consts.AnyCodeNull
import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.quiz.dto.template.QuizTemplateItem
import org.dbs.quiz.enums.TemplateGroupEnum
import org.dbs.quiz.enums.TemplateGroupEnum.entries
import org.dbs.quiz.model.Template
import org.dbs.quiz.model.TemplateGroup
import org.dbs.quiz.repo.TemplateGroupRepo
import org.dbs.quiz.repo.TemplatePageableRepo
import org.dbs.quiz.repo.TemplateRepo
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_TEMPLATE_CODE_2_ENTITY
import org.dbs.service.cache.EntityCacheKeyEnum.CACHE_TEMPLATE_CODE_2_ENTITY_ID
import org.dbs.service.cache.EntityCacheService
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import kotlin.system.measureTimeMillis

@Service
class TemplateDao(
    private val entityCacheService: EntityCacheService<Template>,
    val templateRepo: TemplateRepo,
    val templatePageableRepo: TemplatePageableRepo,
    val templateGroupRepo: TemplateGroupRepo,
) : DaoAbstractApplicationService() {

    suspend fun synchronizeTemplateGroups() = measureTimeMillis {
        fromIterable(templateGroups)
            .publishOn(parallelScheduler)
            .noDuplicates({ it }, { it.groupCode }, { it.groupName })
            .synchronizeReference(templateGroupRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })

        templateGroupRepo.findAll()
            .validateDb { TemplateGroupEnum.Companion.isExistEnum(it.id) }.count().subscribeMono()
    }.also { logger.debug { "synchronizeTemplateGroups: took $it ms" } }
    fun findTemplateByCode(code: String): Mono<Template> =
        entityCacheService.getEntity(CACHE_TEMPLATE_CODE_2_ENTITY, code) {
            templateRepo.findByTemplateCode(code)
        }

    fun invalidateCaches(code: String) {
        runBlocking {
            launch {
                entityCacheService.invalidateCaches(
                    code,
                    CACHE_TEMPLATE_CODE_2_ENTITY,
                    CACHE_TEMPLATE_CODE_2_ENTITY_ID
                )
            }
        }
    }

    fun countAllBy(
        templateCodeMask: AnyCodeNull,
        templateNameMask: AnyCodeNull,
    ): Mono<Int> = templatePageableRepo.countAllBy(templateCodeMask, templateNameMask)

    fun findAllBy(
        templateCodeMask: AnyCodeNull,
        templateNameMask: AnyCodeNull,
        pageable: Pageable
    ): Flux<QuizTemplateItem> = templatePageableRepo.findAllBy(templateCodeMask, templateNameMask, pageable)

    companion object {
        val templateGroups by lazy { entries.map { TemplateGroup(it, it.getGroupCode(), it.getValue()) } }
    }
}
