package org.dbs.auth.server.service

import org.dbs.consts.NoArg2Mono
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Error.INVALID_JWT
import org.dbs.validator.Field
import org.dbs.validator.Field.FLD_ACCESS_JWT
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.model.AbstractJwt
import org.dbs.consts.GenericArg2Unit
import org.dbs.consts.Jwt
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty

object JwtStorageServiceExt {
    private inline fun <AE : AbstractJwt> JwtStorageService.findJwt(
        response: RAB,
        func: org.dbs.consts.NoArg2Mono<AE>,
        jwtNotFoundMessage: String,
        field: Field,
        crossinline successFunc: GenericArg2Unit<AE>,
    ): Mono<AE> = func.invoke()
        .subscribeOn(parallelScheduler)
        .map { successFunc.invoke(it); it }
        .switchIfEmpty {
            response.addErrorInfo(
                RC_INVALID_REQUEST_DATA,
                INVALID_ENTITY_ATTR, field, jwtNotFoundMessage
            )
            empty()
        }

    fun JwtStorageService.findExpiredJwt(
        response: RAB,
        expiredJwt: Jwt,
    ) = expiredJwt.run {
        findJwt(
            response,
            { authServerDao.findExpiredToken(expiredJwt) },
            "unknown expired token ($expiredJwt)",
            FLD_ACCESS_JWT,
            {
                logger.debug("found expired token: ${it.jwt.last15()} ")
                if (it.isRevoked) {
                    response.addErrorInfo(
                        RC_INVALID_REQUEST_DATA,
                        INVALID_JWT, FLD_ACCESS_JWT, "access jwt was revoked: ${it.jwt.last15()}"
                    )
                }
            })
    }

    fun JwtStorageService.findRefreshJwt(
        response: RAB,
        refreshJwt: Jwt,
    ) = refreshJwt.run {
        findJwt(
            response,
            { authServerDao.findRefreshToken(refreshJwt) },
            "unknown refresh token ($refreshJwt)",
            FLD_ACCESS_JWT,
            {
                logger.debug("found refresh token: ${it.jwt.last15()} ")
                if (it.isRevoked) {
                    response.addErrorInfo(
                        RC_INVALID_REQUEST_DATA,
                        INVALID_JWT, FLD_ACCESS_JWT, "refresh jwt was revoked: ${it.jwt.last15()}"
                    )
                }
            })
    }
}
