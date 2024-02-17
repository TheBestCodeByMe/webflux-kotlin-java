package org.dbs.quiz.repo

import org.dbs.consts.AnyCodeNull
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.dto.template.QuizTemplateHistItemDb
import org.dbs.quiz.model.hist.TemplateHist
import org.dbs.quiz.repo.sql.SELECT_COUNT_TEMPLATE_HIST_LIST
import org.dbs.quiz.repo.sql.SELECT_PAGEABLE_TEMPLATE_HIST_LIST
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TemplateHistPageableRepo : ReactiveCrudRepository<TemplateHist, TemplateId> {
    @Query(SELECT_COUNT_TEMPLATE_HIST_LIST)
    fun countAllBy(
        @Param("TC") templateCodeMask: AnyCodeNull,
        @Param("TN") templateNameMask: AnyCodeNull,
    ): Mono<Int>

    @Query(SELECT_PAGEABLE_TEMPLATE_HIST_LIST)
    fun findAllBy(
        @Param("TC") templateCodeMask: AnyCodeNull,
        @Param("TN") templateNameMask: AnyCodeNull,
        pageable: Pageable
    ): Flux<QuizTemplateHistItemDb>

}
