package org.dbs.auth.server.clients.v1.grpc


import org.dbs.ext.FluxFuncs.flatMapSuspend
import org.dbs.validator.Error.ILLEGAL_CALL
import org.dbs.validator.Field.SOME_LOGIN_USER
import org.dbs.application.core.api.LateInitVal
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.enums.ApplicationEnum.S3_USER
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.service.GrpcResponse
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.inTransaction
import org.springframework.http.HttpStatus.OK
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.auth.UserV1LoginRequest as REQ
import org.dbs.protobuf.auth.UserV1LoginResponse as RESP
import org.dbs.protobuf.core.Jwts as E_IN

object GrpcUserV1Login {
    suspend fun AuthServerGrpcService.loginUserV1Internal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run dto@ {

        validateRemoteAddress(remoteAddress)
        val entityBuilder: E_IN.Builder by lazy { E_IN.newBuilder() }
        val grpcResponse: GrpcResponse<RESP> by lazy { { RESP.newBuilder().setResponseAnswer(it).build() } }
        val issuedJwt by lazy { LateInitVal<IssuedJwt>() }
        val refreshJwt by lazy { LateInitVal<RefreshJwt>() }
        val jwtParamsDto by lazy { LateInitVal<JwtParamsDto>() }

        buildGrpcResponse({
            it.run {
                //======================================================================================================
                fun validateRequestData(): Boolean = run {
                    noErrors()
                }

                //======================================================================================================
                fun validateUserV1Credentials(): Mono<RAB> = run {
                    someV1Client.getUserV1Credentials(userLogin, userPassword, this@run).flatMap {
                        if (it.status == OK.value()) {
                            // status user
                            if (!it.message.user.enabled) {
                                addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    ILLEGAL_CALL,
                                    SOME_LOGIN_USER,
                                    "login '${it.message.user.login}' is disabled"
                                )
                            }

                            // userId
                            if (it.message.user.id <= 0) {
                                addErrorInfo(
                                    RC_INVALID_REQUEST_DATA,
                                    ILLEGAL_CALL,
                                    SOME_LOGIN_USER,
                                    "invalid userId - '${it.message.user.id}'"
                                )
                            }

                            if (noErrors()) {
                                jwtParamsDto.hold(someV1Client.toJwtParams(it))
                            }

                        } else {
                            //other error
                            addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                ILLEGAL_CALL,
                                SOME_LOGIN_USER,
                                it.error
                            )
                        }
                        takeIf { noErrors() }.toMono()
                    }
                }

                fun createUserV1Jwt(): Mono<IssuedJwt> =
                    securityService.createAccessJwt(jwtParamsDto.value)
                        .map { it.also { issuedJwt.hold(it) } }


                fun createUserV1RefreshJwt(issuedJwt: IssuedJwt): Mono<RefreshJwt> =
                    securityService.createRefreshJwt(issuedJwt.jwtId, userLogin, remoteAddress)
                        .map { it.also { refreshJwt.hold(it) } }

                suspend fun Mono<RAB>.createAndSaveNewJwts() = flatMapSuspend { rab ->
                    inTransaction {
                        actorsSecurityService.revokeExistsJwt(userLogin, S3_USER)
                            .then(createUserV1Jwt())
                            .flatMap(::createUserV1RefreshJwt)
                            .map { rab }
                    }
                }

                //======================================================================================================
                fun Mono<RAB>.finishResponseEntity() = map {
                    it.also {
                        entityBuilder.setAccessJwt(issuedJwt.value.jwt)
                            .setRefreshJwt(refreshJwt.value.jwt)
                    }
                }
                //======================================================================================================
                // validate requestData
                if (validateRequestData()) {
                    // process endpoint
                    processGrpcResponse {
                        validateUserV1Credentials()
                            .createAndSaveNewJwts()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        })
        { grpcResponse(it) }
    }
}
