package org.dbs.auth.server.clients.v1.service

import org.dbs.auth.server.clients.v1.value.LoginUserV1Request
import org.dbs.auth.server.clients.v1.value.RefreshUserV1JwtRequest
import org.dbs.auth.server.consts.AuthServerConsts.YmlKeys.SOME_V1_ENABLED
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest

@Service
@ConditionalOnProperty(name = [SOME_V1_ENABLED], havingValue = STRING_TRUE)
class SomeV1SecurityRest(
    private val someV1SecurityService: SmartSafeSchoolV1SecurityService,
) : H1_PROCESSOR<ServerRequest>() {

    fun doUserV1Login(serverRequest: ServerRequest) =
        serverRequest.doRequest {
            LoginUserV1Request(it).buildResponse(
                this@SomeV1SecurityRest, someV1SecurityService)
        }

    fun doUserV1RefreshJwt(serverRequest: ServerRequest) =
        serverRequest.doRequest {
            RefreshUserV1JwtRequest(it).buildResponse(
                this@SomeV1SecurityRest, someV1SecurityService)
        }
}
