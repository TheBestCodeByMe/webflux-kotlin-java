package org.dbs.auth.server.service

import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.auth.server.dao.AuthServerDao
import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.auth.server.model.AbstractJwt
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.consts.EntityId
import org.dbs.consts.JwtId
import org.dbs.consts.NoArg2String
import org.dbs.consts.OperDate
import org.dbs.consts.SysConst.LOCALDATETIME_NULL
import org.springframework.beans.factory.annotation.Value
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Service
class JwtStorageService(
    val authServerDao: AuthServerDao,
    private val databaseClient: DatabaseClient
) : AbstractApplicationService() {

    @Value("\${spring.r2dbc.next-token-cmd:SELECT nextval('seq_tnk_card_id')}")
    private val nextTokenCmd = "SELECT null"

    fun <T : AbstractJwt> generateNewJwtId(entClass: Class<T>): Mono<EntityId> =
        databaseClient.sql(nextTokenCmd)
            .map { row -> row.get(0, java.lang.Long::class.java) } //
            .one()
            .map { newId ->
                logger.debug("generate new jwtId: $newId [${entClass.canonicalName}]")
                newId.toLong()
            }

    final inline fun createAndSaveAccessJwt(
        tokenKey: String,
        validUntil: LocalDateTime,
        application: ApplicationEnum,
        jwtOwner: String,
        crossinline tokenBuilder: NoArg2String
    ): Mono<IssuedJwt> = generateNewJwtId(IssuedJwt::class.java)
        .flatMap { newEntityId ->

            // remove old issued tokens
            logger.debug("Revoke previous tokens for '$jwtOwner' (application=$application)")
            authServerDao.revokeExistsJwt(jwtOwner, application)
                .then(authServerDao.saveIssuedToken(
                    IssuedJwt(
                        jwtId = newEntityId,
                        applicationId = application,
                        issueDate = now(),
                        validUntil = validUntil,
                        jwt = tokenBuilder.invoke(),
                        issuedTo = jwtOwner,
                        tag = tokenKey,
                        isRevoked = false,
                        revokeDate = LOCALDATETIME_NULL
                    ).asNew<IssuedJwt>()
                        .also {
                            logger.debug("issue new access jwt for '$jwtOwner' ($tokenKey), application=$application")
                        }
                ))
        }

    final inline fun createAndSaveRefreshJwt(
        parentJwtId: JwtId,
        validUntil: LocalDateTime,
        tokenKey: String,
        crossinline tokenBuilder: NoArg2String
    ): Mono<RefreshJwt> = generateNewJwtId(RefreshJwt::class.java)
        .flatMap { newEntityId ->
            logger.info("issue new refresh token for '$tokenKey'")
            authServerDao.saveRefreshToken(
                RefreshJwt(
                    jwtId = newEntityId,
                    issueDate = now(),
                    validUntil = validUntil,
                    jwt = tokenBuilder.invoke(),
                    parentJwtId = parentJwtId,
                    isRevoked = false,
                    revokeDate = LOCALDATETIME_NULL
                ).asNew()
            )
        }

    fun revokeExistsJwt(jwtOwner: String, application: ApplicationEnum) =
        authServerDao.revokeExistsJwt(jwtOwner, application)

    fun deleteDeprecatedJwt(deprecateDate: OperDate) = authServerDao.deleteDeprecatedJwt(deprecateDate)
    fun arcDeprecatedJwt(deprecateDate: OperDate) = authServerDao.arcDeprecatedJwt(deprecateDate)

    fun moveJwt2Arc(expiredIssuedJwt: IssuedJwt) = authServerDao.moveJwt2Arc(expiredIssuedJwt)
    fun moveRefreshJwt2Arc(obsoleteRefreshJwt: RefreshJwt) = authServerDao.moveRefreshJwt2Arc(obsoleteRefreshJwt)

}
