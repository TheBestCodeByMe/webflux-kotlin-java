package org.dbs.auth.server.v1

import org.dbs.auth.server.v1.dto.UserV1DtoResponse
import reactor.core.publisher.Mono

@Deprecated("remove")
fun interface UserV1Login {
    fun findUserV1Login(userV1Login: String, userV1Password: String): Mono<UserV1DtoResponse>
}
