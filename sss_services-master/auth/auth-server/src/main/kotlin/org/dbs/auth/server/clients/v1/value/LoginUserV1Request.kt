package org.dbs.auth.server.clients.v1.value

import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.auth.server.clients.v1.grpc.GrpcUserV1Login.loginUserV1Internal
import org.dbs.auth.server.clients.v1.service.SmartSafeSchoolV1SecurityService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.grpc.api.H1h2.Companion.applyH1Converter
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.auth.server.clients.v1.grpc.convert.UserV1LoginConverter as H1_CONV
import org.dbs.rest.dto.value.LoginUserDto as IN_DTO
import org.dbs.rest.dto.value.LoginUserRequest as H1_REQ
import org.dbs.rest.dto.value.LoginUserResponse as H1_RES
import org.dbs.rest.dto.value.IssuedJwtResultDto as OUT_DTO

@JvmInline
value class LoginUserV1Request<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<IN_DTO, H1_REQ, OUT_DTO, H1_RES> {
    suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
        securityService: SmartSafeSchoolV1SecurityService,
    ) = processor.createResponse(
        serverRequest,
        H1_REQ::class.java,
        H1_RES::class.java
    ) {
        serverRequest.run {
            securityService.buildMonoResponse(this, H1_REQ::class.java)
            {
                applyH1Converter(H1_CONV::class, it, id())
                {
                    with(authServerGrpcService) {
                        loginUserV1Internal(it, ip())
                    }
                }
            }
        }
    }

    companion object {
        val authServerGrpcService by lazy { findService(AuthServerGrpcService::class.java) }
    }
}
