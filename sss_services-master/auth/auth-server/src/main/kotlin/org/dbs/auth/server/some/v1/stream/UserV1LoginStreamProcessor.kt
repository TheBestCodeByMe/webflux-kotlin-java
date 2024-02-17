package org.dbs.auth.server.v1.stream

import org.dbs.application.core.api.LateInitVal
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.v1.dto.UserV1DtoResponse
import org.dbs.rest.api.PostStreamProcessor
import org.dbs.rest.dto.login.CreateLoginRequest
import org.dbs.rest.dto.login.CreatedLogin
import org.dbs.rest.dto.login.CreatedLoginResponse
import org.springframework.web.reactive.function.server.ServerRequest

@Deprecated("remove")
data class UserV1LoginStreamProcessor(
    private val serverRequest: ServerRequest,
    private val createLoginRequest: CreateLoginRequest,
) : PostStreamProcessor<CreateLoginRequest, CreatedLogin, CreatedLoginResponse>(serverRequest, createLoginRequest) {
    val userV1Dto by lazy { LateInitVal<UserV1DtoResponse>() }
    val jwtParams by lazy { LateInitVal<JwtParamsDto>() }
    val spUserLogin by lazy { LateInitVal<String>() }
    val spUserPass by lazy { LateInitVal<String>() }
    val ip by lazy { LateInitVal<String>() }
    val jwtId by lazy { LateInitVal<Long>() }
    val issuedJwt by lazy { LateInitVal<String>() }
    val refreshJwt by lazy { LateInitVal<String>() }
}
