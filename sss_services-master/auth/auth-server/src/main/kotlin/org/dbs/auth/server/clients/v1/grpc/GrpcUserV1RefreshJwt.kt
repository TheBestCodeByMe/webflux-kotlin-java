package org.dbs.auth.server.clients.v1.grpc


import org.dbs.application.core.api.LateInitVal
import org.dbs.auth.server.JwtParamsDto
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
import org.dbs.auth.server.enums.ApplicationEnum.S3_USER
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.JwtStorageServiceExt.findExpiredJwt
import org.dbs.auth.server.service.JwtStorageServiceExt.findRefreshJwt
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.consts.Address
import org.dbs.consts.Email
import org.dbs.consts.EntityId
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.consts.Login
import org.dbs.ext.FluxFuncs.flatMapSuspend
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.GrpcResponse
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.inTransaction
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field
import org.dbs.validator.Field.FLD_ACCESS_JWT
import org.dbs.validator.Field.FLD_REFRESH_JWT
import org.dbs.validator.Field.SSS_LOGIN_USER
import org.dbs.validator.Field.SSS_USER_FIRST_NAME
import org.dbs.validator.Field.SSS_USER_ID
import org.dbs.validator.Field.SSS_USER_LAST_NAME
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.auth.RefreshUserV1JwtRequest as REQ
import org.dbs.protobuf.auth.RefreshUserV1JwtResponse as RESP
import org.dbs.protobuf.core.Jwts as E_IN


object GrpcUserV1RefreshJwt {
    suspend fun AuthServerGrpcService.userV1RefreshJwtInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run {

        validateRemoteAddress(remoteAddress)
        val entityBuilder: E_IN.Builder by lazy { E_IN.newBuilder() }
        val grpcResponse: GrpcResponse<RESP> by lazy { { RESP.newBuilder().setResponseAnswer(it).build() } }
        val userV1Login by lazy { LateInitVal<Login>() }
        val userV1Id by lazy { LateInitVal<EntityId>() }
        val userFirstName by lazy { LateInitVal<String>() }
        val userLastName by lazy { LateInitVal<String>() }
        val userPhone by lazy { LateInitVal<String>() }
        val userEmail by lazy { LateInitVal<Email>() }
        val userAddress by lazy { LateInitVal<Address>() }
        val userRoles by lazy { LateInitVal<String>() }
        val issuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val refreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val existsExpiredIssuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val existsRefreshJwt by lazy { LateInitVal<RefreshJwt>() }

        buildGrpcResponse({
            it.run {
                //======================================================================================================
                fun validateRequestBody(): Boolean = run {

                    // validate access jwt
                    jwts.accessJwt.ifEmpty {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            FLD_ACCESS_JWT,
                            "access jwt not specified"
                        )
                    }

                    // validate refresh jwt
                    jwts.refreshJwt.ifEmpty {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            FLD_REFRESH_JWT,
                            "refresh jwt not specified"
                        )
                    }
                    noErrors()
                }

                fun validateExpiredClaim(claim: String, field: Field,  func: (String) -> Unit) {
                    jwtSecurityService.getClaimExpired(jwts.accessJwt, claim)?.let {  func.invoke(it) } ?: run {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            field,
                            "unknown or invalid claim ($claim) in jwt claims [${jwts.accessJwt}]"
                        )
                    }
                }

                fun validateClaim(claim: String, field: Field,  func: (String) -> Unit) {
                    jwtSecurityService.getClaimExpired(jwts.refreshJwt, claim)?.let {  func.invoke(it) } ?: run {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            field,
                            "unknown or invalid claim ($claim) in jwt claims [${jwts.accessJwt}]"
                        )
                    }
                }

                fun validateUserV1Credentials(): Mono<RAB> = run {
                    validateExpiredClaim(CL_USER_ID, SSS_USER_ID) { userV1Id.hold(it.toLong()) }
                    validateExpiredClaim(CL_USER_FIRST_NAME, SSS_USER_FIRST_NAME) { userFirstName.hold(it) }
                    jwtSecurityService.getClaimExpired(jwts.accessJwt, CL_USER_ADDRESS)?.let { userAddress.hold(it) }
                    jwtSecurityService.getClaimExpired(jwts.accessJwt, CL_ROLES)?.let { userRoles.hold(it) }
                    validateExpiredClaim(CL_USER_LAST_NAME, SSS_USER_LAST_NAME) { userLastName.hold(it) }
                    validateClaim(CL_USER_LOGIN, SSS_LOGIN_USER) { userV1Login.hold(it) }
                    jwtSecurityService.getClaimExpired(jwts.accessJwt, CL_USER_PHONE)?.let { userPhone.hold(it) }
                    jwtSecurityService.getClaimExpired(jwts.accessJwt, CL_USER_EMAIL)?.let { userEmail.hold(it) }

                    toMono()
                }

                fun moveObsoletJwtsToArc(newRefreshJwt: RefreshJwt) = newRefreshJwt.run {
                    securityService.jwtStorageService.moveJwt2Arc(existsExpiredIssuedJwt.value)
                        .flatMap { securityService.jwtStorageService.moveRefreshJwt2Arc(existsRefreshJwt.value) }
                        .then(newRefreshJwt.toMono())
                }

                fun Mono<RAB>.validateExpiredAccessJwt() = flatMap { ab ->
                    securityService.jwtStorageService.findExpiredJwt(it, jwts.accessJwt)
                        .map { existsExpiredIssuedJwt.hold(it); ab }
                }

                fun createUserV1Jwt(): Mono<IssuedJwt> =
                    securityService.createAccessJwt(
                        JwtParamsDto(
                            userId = userV1Id.value,
                            userLogin = userV1Login.value,
                            address = userAddress.valueOrNull,
                            email = userEmail.valueOrNull,
                            firstName = userFirstName.value,
                            lastName = userLastName.value,
                            phone = userPhone.valueOrNull,
                            roles = userRoles.value
                        )
                    ).map { it.also { issuedJwt.hold(it) } }

                fun createUserV1RefreshJwt(issuedJwt: IssuedJwt) =
                    securityService.createRefreshJwt(issuedJwt.jwtId, userV1Login.value, remoteAddress)
                        .map { it.also { refreshJwt.hold(it) } }

                fun Mono<RAB>.validateExistsRefreshJwt() = flatMap { ab ->
                    securityService.jwtStorageService.findRefreshJwt(it, jwts.refreshJwt)
                        .map {
                            existsRefreshJwt.hold(it)

                            if (existsRefreshJwt.value.parentJwtId != existsExpiredIssuedJwt.value.jwtId) {
                                addErrorInfo(
                                    RC_INVALID_RESPONSE_DATA,
                                    INVALID_ENTITY_ATTR,
                                    FLD_REFRESH_JWT,
                                    "invalid token pair (issuedJwtId = ${existsExpiredIssuedJwt.value.jwtId},"
                                )
                            }
                            ab
                        }
                }

                suspend fun Mono<RAB>.createAndSaveNewJwts() = flatMapSuspend { ab ->
                    inTransaction {
                        securityService.jwtStorageService.revokeExistsJwt(
                            userV1Login.value,
                            S3_USER
                        )
                            .then(createUserV1Jwt())
                            .flatMap(::createUserV1RefreshJwt)
                            .flatMap(::moveObsoletJwtsToArc)
                            .map { ab }
                    }
                }

                fun Mono<RAB>.finishResponseEntity() = map {
                    it.also {
                        entityBuilder.setAccessJwt(issuedJwt.value.jwt)
                            .setRefreshJwt(refreshJwt.value.jwt)
                    }
                }

                if (validateRequestBody()) {
                    processGrpcResponse {
                        validateUserV1Credentials()
                            .validateExpiredAccessJwt()
                            .validateExistsRefreshJwt()
                            .createAndSaveNewJwts()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        }) { grpcResponse(it) }
    }
}
