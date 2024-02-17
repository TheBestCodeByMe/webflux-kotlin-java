package org.dbs.quiz.repo

import org.dbs.consts.EntityCode
import org.dbs.quiz.consts.TemplateId
import org.dbs.quiz.model.Template
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface TemplateRepo : R2dbcRepository<Template, TemplateId> {
    fun findByTemplateCode(login: EntityCode): Mono<Template>
    //fun findByEmail(email: Email): Mono<Template>
}
