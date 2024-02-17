package org.dbs.auth.server.dao

import org.dbs.ext.FluxFuncs.noDuplicates
import org.dbs.ext.FluxFuncs.subscribeMono
import org.dbs.ext.FluxFuncs.validateDb
import org.dbs.spring.core.api.DaoAbstractApplicationService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.enums.ApplicationEnum.Companion.isExistEnum
import org.dbs.auth.server.enums.ApplicationEnum.entries
import org.dbs.auth.server.model.Application
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.IssuedJwtArc
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.model.RefreshJwtArc
import org.dbs.auth.server.repo.ApplicationRepo
import org.dbs.auth.server.repo.IssuedJwtArcRepo
import org.dbs.auth.server.repo.IssuedJwtRepo
import org.dbs.auth.server.repo.RefreshJwtArcRepo
import org.dbs.auth.server.repo.RefreshJwtRepo
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.dbs.service.api.RefSyncFuncs.synchronizeReference
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import kotlin.system.measureTimeMillis

@Service
@Lazy(false)
class AuthServerDao(
    private val issuedJwtRepo: IssuedJwtRepo,
    private val issuedJwtArcRepo: IssuedJwtArcRepo,
    private val refreshJwtRepo: RefreshJwtRepo,
    private val refreshJwtArcRepo: RefreshJwtArcRepo,
    private val applicationRepo: ApplicationRepo,
) : DaoAbstractApplicationService() {

    val actualApplications: Collection<Application> by lazy {
        entries.map { Application(it.getApplicationId(), it.name, it.getApplicationName()) }
            .toList()
    }

    suspend fun synchronizeRefs() = measureTimeMillis {
        fromIterable(actualApplications)
            .publishOn(parallelScheduler)
            .noDuplicates({ it.applicationId }, { it.applicationCode })
            .synchronizeReference(applicationRepo,
                { existItem, preparedItem -> existItem.id == preparedItem.id },
                { preparedItem -> preparedItem.copy() })
        applicationRepo.findAll().validateDb { isExistEnum(it.id) }.count().subscribeMono()
    }.also { logger.debug { "synchronizeRefs: took $it ms" } }

    fun moveJwt2Arc(expiredIssuedJwt: IssuedJwt) =
        expiredIssuedJwt.run {
            createIssuedArcToken(
                IssuedJwtArc(
                    jwtId = jwtId,
                    issueDate = issueDate,
                    validUntil = validUntil,
                    jwt = jwt,
                    applicationId = applicationId,
                    issuedTo = issuedTo,
                    tag = tag,
                    isRevoked = isRevoked,
                    revokeDate = revokeDate
                ).asNew()
            )
        }

    fun moveRefreshJwt2Arc(obsoleteRefreshJwt: RefreshJwt) =
        obsoleteRefreshJwt.run {
            createRefreshArcToken(
                RefreshJwtArc(
                    jwtId = jwtId,
                    issueDate = issueDate,
                    validUntil = validUntil,
                    jwt = jwt,
                    refreshDate = LocalDateTime.now(),
                    parentJwtId = parentJwtId,
                    isRevoked = isRevoked,
                    revokeDate = revokeDate
                ).asNew()
            ).flatMap { deleteDeprecatedJwt(jwtId, parentJwtId) }
        }

    private fun deleteDeprecatedJwt(obsoleteRefreshJwtId: JwtId, expiredIssueJwtId: JwtId) = run {
        deleteRefreshToken(obsoleteRefreshJwtId)
            .thenEmpty(deleteIssuedToken(expiredIssueJwtId))
    }

    fun saveIssuedToken(issuedJwt: IssuedJwt): Mono<IssuedJwt> = issuedJwtRepo.save(issuedJwt)
    fun deleteIssuedToken(jwtId: JwtId): Mono<Void> = issuedJwtRepo.deleteById(jwtId)
    fun createIssuedArcToken(issuedJwtArc: IssuedJwtArc): Mono<IssuedJwtArc> = issuedJwtArcRepo.save(issuedJwtArc)
    fun saveRefreshToken(refreshJwt: RefreshJwt): Mono<RefreshJwt> = refreshJwtRepo.save(refreshJwt)
    fun deleteRefreshToken(jwtId: JwtId): Mono<Void> = refreshJwtRepo.deleteById(jwtId)
    fun createRefreshArcToken(refreshJwtArc: RefreshJwtArc): Mono<RefreshJwtArc> = refreshJwtArcRepo.save(refreshJwtArc)
    fun findExpiredToken(jwt: Jwt): Mono<IssuedJwt> = issuedJwtRepo.findByJwtUnique(jwt)
    fun findRefreshToken(jwt: Jwt): Mono<RefreshJwt> = refreshJwtRepo.findByJwtUnique(jwt)
    fun deleteDeprecatedJwt(deprecateDate: OperDate) = issuedJwtRepo.deleteDeprecatedJwt(deprecateDate)
    fun arcDeprecatedJwt(deprecateDate: OperDate) = issuedJwtRepo.arcDeprecatedJwt(deprecateDate)
    fun revokeExistsJwt(jwtOwner: String, application: ApplicationEnum) = issuedJwtRepo.revokeJwt(jwtOwner, application)
    override fun initialize() = super.initialize().also { runBlocking { launch { synchronizeRefs() } } }
}
