package org.dbs.auth.server.v1.validator

import org.dbs.consts.NoArg2Mono
import org.dbs.ext.CollectionFuncs.whenNoErrors
import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field
import org.dbs.validator.Field.FLD_ACCESS_JWT
import org.dbs.validator.Field.FLD_HOST_ADDRESS
import org.dbs.validator.Field.FLD_REFRESH_JWT
import org.dbs.validator.Field.SSS_LOGIN_USER
import org.dbs.validator.Field.SSS_USER_FIRST_NAME
import org.dbs.validator.Field.SSS_USER_ID
import org.dbs.validator.Field.SSS_USER_LAST_NAME
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.dao.AuthServerDao
import org.dbs.auth.server.model.AbstractJwt
import org.dbs.auth.server.v1.stream.UserV1RefreshJwtStreamProcessor
import org.dbs.component.JwtSecurityService
import org.dbs.consts.GenericArg2Unit
import org.dbs.consts.Jwt
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.dbs.consts.SysConst.UNKNOWN
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_ILLEGAL_REFRESH_TOKEN
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_FIRST_NAME
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_ID
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_LAST_NAME
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_LOGIN
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_EXPIRED_TOKEN
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_OR_INVALID_ADDRESS
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNKNOWN_REFRESH_TOKEN
import org.dbs.rest.api.validator.AbstractStreamValidatorService
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_SCHOOL_ID
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_ROLES
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_SCHOOL_TZ
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_ADDRESS
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_EMAIL
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_FIRST_NAME
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_ID
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_LAST_NAME
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_LOGIN
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_PHONE
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Deprecated("remove")
@Service
data class UserV1RefreshJwtValidator(
    private val authServerDao: AuthServerDao
) : AbstractStreamValidatorService<UserV1RefreshJwtStreamProcessor>() {

    private val jwtSecurityService by lazy { findService(JwtSecurityService::class.java) }

    override fun validate(
        serverRequest: ServerRequest,
        streamProcessor: UserV1RefreshJwtStreamProcessor
    ): Mono<UserV1RefreshJwtStreamProcessor> =
        streamProcessor.run {
            ip.hold(serverRequest.ip())
            with(requestBody.entityInfo) {
                // validate IP
                validateIp(serverRequest)
                validateJwtUserIdClaim(streamProcessor, expiredJwt)
                validateJwtFirstNameClaim(streamProcessor, expiredJwt)
                validateJwtLastNameClaim(streamProcessor, expiredJwt)
                validateJwtLoginClaim(streamProcessor, refreshJwt)
                jwtSecurityService.getClaimExpired(expiredJwt, CL_USER_PHONE)?.let { userPhone = it }
                jwtSecurityService.getClaimExpired(expiredJwt, CL_USER_EMAIL)?.let { userEmail = it }
                jwtSecurityService.getClaimExpired(expiredJwt, CL_USER_ADDRESS)?.let { userAddress = it }
                jwtSecurityService.getClaimExpired(expiredJwt, CL_SCHOOL_ID)?.let { schoolId = it }
                schoolTimeZone = jwtSecurityService.getClaimExpired(expiredJwt, CL_SCHOOL_TZ)?.toInt() ?: 0
                jwtSecurityService.getClaimExpired(expiredJwt, CL_ROLES)?.let { roles = it }
            }
            responseBody.errors.whenNoErrors {
                jwtParams.hold(getJwtParams())
                findExpiredToken(this)
                    .flatMap(::findRefreshToken)
                    .doOnError { throwable -> registerException(throwable, this, FLD_REFRESH_JWT) }
                    .onErrorResume { empty() }
            }
        }

    private fun UserV1RefreshJwtStreamProcessor.getJwtParams() = JwtParamsDto(
        userId = userId.value,
        userLogin = userLogin.value,
        firstName = userFirstName.value,
        lastName = userLastName.value,
        phone = userPhone,
        email = userEmail,
        address = userAddress,
        schoolId = schoolId,
        roles = roles,
        schoolTimeZone = schoolTimeZone
    )

    private fun findExpiredToken(refreshJwtStreamProcessor: UserV1RefreshJwtStreamProcessor): Mono<UserV1RefreshJwtStreamProcessor> =
        refreshJwtStreamProcessor.run {
            findJwt(
                this,
                { authServerDao.findExpiredToken(requestBody.entityInfo.expiredJwt) },
                "unknown expired token (${requestBody.entityInfo.expiredJwt})",
                OC_UNKNOWN_EXPIRED_TOKEN,
                FLD_ACCESS_JWT,
                { expiredIssuedJwt.hold(it); logger.debug("found expired token: ${it.jwt.last15()} ") })
        }

    private fun findRefreshToken(refreshJwtStreamProcessor: UserV1RefreshJwtStreamProcessor): Mono<UserV1RefreshJwtStreamProcessor> =
        refreshJwtStreamProcessor.run {
            findJwt(
                this,
                { authServerDao.findRefreshToken(requestBody.entityInfo.refreshJwt) },
                "unknown expired token (${requestBody.entityInfo.expiredJwt})",
                OC_UNKNOWN_REFRESH_TOKEN,
                FLD_REFRESH_JWT,
                {
                    logger.debug("found refresh token: ${it.jwt.last15()}")
                    expiredRefreshJwt.hold(it)
                    // validate relations between 2 tokens (access & refresh)
                    if (expiredRefreshJwt.value.parentJwtId != expiredIssuedJwt.value.jwtId) {
                        addErrorInfo(
                            OC_ILLEGAL_REFRESH_TOKEN,
                            INVALID_ENTITY_ATTR,
                            FLD_REFRESH_JWT,
                            "invalid token pair (issuedJwtId = ${expiredIssuedJwt.value.jwtId}, " +
                                    "RefreshJwt.parentJwtId = ${expiredRefreshJwt.value.parentJwtId}), " +
                                    "applied refreshJwt.jwtId = ${expiredRefreshJwt.value.jwtId}"
                        )
                    }
                })
        }

    @Suppress(UNCHECKED_CAST)
    private inline fun <AE : AbstractJwt> findJwt(
        refreshJwtStreamProcessor: UserV1RefreshJwtStreamProcessor,
        func: org.dbs.consts.NoArg2Mono<AE>,
        entityNotFoundMessage: String,
        restOperCodeEnum: RestOperCodeEnum,
        field: Field,
        crossinline successFunc: GenericArg2Unit<AE>
    ): Mono<UserV1RefreshJwtStreamProcessor> =
        refreshJwtStreamProcessor.run {
            func.invoke()
                .subscribeOn(parallelScheduler)
                .map { successFunc.invoke(it); this }
                .switchIfEmpty {
                    addErrorInfo(
                        restOperCodeEnum,
                        INVALID_ENTITY_ATTR, field, entityNotFoundMessage
                    )
                    this.toMono()
                }
        }

    private fun validateJwtFirstNameClaim(sp: UserV1RefreshJwtStreamProcessor, token: String) {
        jwtSecurityService.getClaimExpired(token, CL_USER_FIRST_NAME)?.let {
            sp.userFirstName.hold(it)
        } ?: run {
            sp.addErrorInfo(
                OC_INVALID_USER_FIRST_NAME,
                INVALID_ENTITY_ATTR,
                SSS_USER_FIRST_NAME,
                "unknown or invalid user first name in jwt claims [$token]"
            )
        }
    }

    private fun validateJwtLastNameClaim(sp: UserV1RefreshJwtStreamProcessor, token: String) {
        jwtSecurityService.getClaimExpired(token, CL_USER_LAST_NAME)?.let {
            sp.userLastName.hold(it)
        } ?: run {
            sp.addErrorInfo(
                OC_INVALID_USER_LAST_NAME,
                INVALID_ENTITY_ATTR,
                SSS_USER_LAST_NAME,
                "unknown or invalid user last name in jwt claims [$token]"
            )
        }
    }

    private fun validateJwtLoginClaim(sp: UserV1RefreshJwtStreamProcessor, jwt: Jwt) {
        jwtSecurityService.getClaim(jwt, CL_USER_LOGIN)?.let {
            sp.userLogin.hold(it)
        } ?: run {
            sp.addErrorInfo(
                OC_INVALID_USER_LOGIN,
                INVALID_ENTITY_ATTR,
                SSS_LOGIN_USER,
                "unknown or invalid user login in jwt claims [$jwt]"
            )
        }
    }

    private fun validateJwtUserIdClaim(sp: UserV1RefreshJwtStreamProcessor, jwt: Jwt) {
        jwtSecurityService.getClaimExpired(jwt, CL_USER_ID)?.let {
            sp.userId.hold(it.toLong())
        } ?: run {
            sp.addErrorInfo(
                OC_INVALID_USER_ID,
                INVALID_ENTITY_ATTR,
                SSS_USER_ID,
                "unknown or invalid user id in jwt claims [$jwt]"
            )
        }
    }

    private fun UserV1RefreshJwtStreamProcessor.validateIp(serverRequest: ServerRequest) {
        if (ip.value == UNKNOWN) {
            addErrorInfo(
                OC_UNKNOWN_OR_INVALID_ADDRESS,
                INVALID_ENTITY_ATTR,
                FLD_HOST_ADDRESS,
                "unknown or invalid host address (${serverRequest.remoteAddress().get()})"
            )
        }
    }
}
