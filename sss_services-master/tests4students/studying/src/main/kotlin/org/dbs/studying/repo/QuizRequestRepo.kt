package org.dbs.quiz.repo

import org.dbs.consts.CustomerId
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.QuizRequestId
import org.dbs.quiz.consts.TemplateCode
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.dto.quiz.response.QuizRequestInfoDb
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.repo.sql.SELECT_REQUESTS_BY_CUSTOMERS_TEMPLATE
import org.dbs.quiz.repo.sql.SELECT_REQUESTS_BY_TEMPLATE_CODE
import org.dbs.quiz.repo.sql.SELECT_REQUEST_INFO_4_QUIZ_RESPONSE
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface QuizRequestRepo : R2dbcRepository<QuizRequest, QuizRequestId> {
    @Query(SELECT_REQUESTS_BY_TEMPLATE_CODE)
    fun findByTemplateCode(@Param("TEMP") templateCode: TemplateCode): Flux<QuizRequest>

    @Query(SELECT_REQUESTS_BY_CUSTOMERS_TEMPLATE)
    fun findByCustomerIdsAndTemplateId(
        @Param("IDs") customerId: Collection<CustomerId>,
        @Param("TEMP") templateId: TemplateId
    ): Flux<QuizRequest>

    fun findByRequestCode(quizRequestCode: QuizRequestCode): Mono<QuizRequest>

    @Query(SELECT_REQUEST_INFO_4_QUIZ_RESPONSE)
    fun findInfo4QuizResponse(@Param("CODE") quizRequestCode: QuizRequestCode): Mono<QuizRequestInfoDb>
}
