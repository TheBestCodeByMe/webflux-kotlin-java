package org.dbs.auth.server.v1.stream

import org.dbs.application.core.api.LateInitVal
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.consts.Jwt
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.rest.api.PostStreamProcessor
import org.dbs.rest.dto.jwt.CreateJwtRequest
import org.dbs.rest.dto.jwt.CreatedJwt
import org.dbs.rest.dto.jwt.CreatedJwtResponse
import org.springframework.web.reactive.function.server.ServerRequest

@Deprecated("remove")
data class UserV1RefreshJwtStreamProcessor(
    private val serverRequest: ServerRequest,
    private val createJwtRequest: CreateJwtRequest,
) : PostStreamProcessor<CreateJwtRequest, CreatedJwt, CreatedJwtResponse>(serverRequest, createJwtRequest) {
    val ip by lazy { LateInitVal<String>() }
    val jwtId by lazy { LateInitVal<Long>() }
    val jwtParams by lazy { LateInitVal<JwtParamsDto>() }
    val userLogin by lazy { LateInitVal<String>() }
    val userFirstName by lazy { LateInitVal<String>() }
    val userLastName by lazy { LateInitVal<String>() }
    var userPhone: String? = null
    var userEmail: String? = null
    var userAddress: String? = null
    var someId: String? = null
    var someTimeZone: Int = 0
    var roles: String = EMPTY_STRING
    val userId by lazy { LateInitVal<Long>() }
    val issuedJwt by lazy { LateInitVal<Jwt>() }
    val refreshJwt by lazy { LateInitVal<Jwt>() }
    val expiredIssuedJwt by lazy { LateInitVal<IssuedJwt>() }
    val expiredRefreshJwt by lazy { LateInitVal<RefreshJwt>() }
}
