package org.dbs.quiz.dao

import org.dbs.consts.AnyCodeNull
import org.dbs.quiz.dto.template.QuizTemplateHistItemDb
import org.dbs.quiz.repo.TemplateHistPageableRepo
import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TemplateHistDao(
    val templateHistPageableRepo: TemplateHistPageableRepo,
) : DaoAbstractApplicationService() {

    fun countAllBy(
        templateCodeMask: AnyCodeNull,
        templateNameMask: AnyCodeNull,
    ): Mono<Int> = templateHistPageableRepo.countAllBy(templateCodeMask, templateNameMask)

    fun findAllBy(
        templateCodeMask: AnyCodeNull,
        templateNameMask: AnyCodeNull,
        pageable: Pageable
    ): Flux<QuizTemplateHistItemDb> = templateHistPageableRepo.findAllBy(templateCodeMask, templateNameMask, pageable)
}
