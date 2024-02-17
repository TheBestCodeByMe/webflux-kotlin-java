package org.dbs.auth.server

import org.dbs.store.consts.FirstName
import org.dbs.store.consts.LastName

data class JwtParamsDto(
    val userId: Long,
    val userLogin: String,
    val firstName: FirstName,
    val lastName: LastName,
    val phone: String?,
    val email: String?,
    val address: String?,
    val roles: String,
)
