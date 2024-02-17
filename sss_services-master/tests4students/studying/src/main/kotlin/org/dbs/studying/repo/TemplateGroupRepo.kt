package org.dbs.quiz.repo

import org.dbs.consts.ReferenceId
import org.dbs.quiz.model.TemplateGroup
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface TemplateGroupRepo : R2dbcRepository<TemplateGroup, ReferenceId>
