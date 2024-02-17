package org.dbs.quiz.repo

import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.consts.QuizResponseId
import org.dbs.quiz.model.QuizResponse
import org.dbs.quiz.repo.sql.SELECT_QUIZ_RESPONSES_BY_QUIZ_REQUEST_CODE
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface QuizResponseRepo : R2dbcRepository<QuizResponse, QuizResponseId> {
    @Query(SELECT_QUIZ_RESPONSES_BY_QUIZ_REQUEST_CODE)
    fun findByRequestCode(quizRequestCode: QuizRequestCode): Flux<QuizResponse>
}
