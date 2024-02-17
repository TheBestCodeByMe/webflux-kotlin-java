package org.dbs.auth.server.dao

import org.dbs.spring.core.api.DaoAbstractApplicationService
import org.dbs.auth.server.model.HttpServerRequest
import org.dbs.auth.server.repo.HttpServerRequestRepo
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux


@Service

class HttpServerRequestsDao(httpServerRequestRepo: HttpServerRequestRepo) : DaoAbstractApplicationService() {
    final val httpServerRequestRepo: HttpServerRequestRepo

    init {
        this.httpServerRequestRepo = httpServerRequestRepo
    }

    fun saveRequests(requests: Collection<HttpServerRequest>): Flux<HttpServerRequest> =
        httpServerRequestRepo.saveAll<HttpServerRequest>(Flux.fromStream(requests.stream()))
}
