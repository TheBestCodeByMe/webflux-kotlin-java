package org.dbs.auth.server.clients.v1.value

import org.dbs.spring.core.api.ServiceLocator.findService
import org.dbs.auth.server.clients.v1.grpc.GrpcUserV1RefreshJwt.userV1RefreshJwtInternal
import org.dbs.auth.server.clients.v1.service.SmartSafeSchoolV1SecurityService
import org.dbs.auth.server.service.grpc.AuthServerGrpcService
import org.dbs.grpc.api.H1h2
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.dto.value.JwtList
import org.dbs.rest.dto.value.IssuedJwtResultDto
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.auth.server.clients.v1.grpc.convert.UserV1RefreshJwtConverter as CONV
import org.dbs.rest.dto.value.RefreshJwtRequest as REQ
import org.dbs.rest.dto.value.RefreshJwtResponse as RESP

@JvmInline
value class RefreshUserV1JwtRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>,
    HttpReactivePostRequest<JwtList, REQ, IssuedJwtResultDto, RESP> {
    suspend fun buildResponse(
        processor: H1_PROCESSOR<R>,
        securityService: SmartSafeSchoolV1SecurityService,
    ) = processor.createResponse(
        serverRequest,
        REQ::class.java,
        RESP::class.java
    ) {
        serverRequest.run {
            securityService.buildMonoResponse(this, REQ::class.java)
            {
                H1h2.applyH1Converter(CONV::class, it, id())
                { with(authServerGrpcService) { userV1RefreshJwtInternal(it, ip()) } }
            }
        }
    }

    companion object {
        val authServerGrpcService by lazy { findService(AuthServerGrpcService::class.java) }
    }
}
