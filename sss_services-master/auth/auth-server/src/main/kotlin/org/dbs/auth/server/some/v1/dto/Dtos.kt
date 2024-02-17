package org.dbs.auth.server.v1.dto

import org.dbs.consts.Email
import org.dbs.consts.EntityId

class Dto {
}

data class UserV1DtoResponse(
    val status: Int,
    val message: UserV1Message,
    val error: String,
    val timestamp: String
)

data class UserV1Dto(
    val id: EntityId,
    val login: String,
    val email: Email?,
    val photo: String?,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val birthday: String,
    val phone: String?,
    val gender: String?,
    val enabled: Boolean
)

data class UserV1Message(
    val user: UserV1Details,
    val schoolId: Long?,
    val schoolTimeZone: Int = 0
)

data class UserV1Details(
    val id: Long,
    val login: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val phone: String?,
    val address: List<UserV1Address>,
    val enabled: Boolean,
    val roles: List<String>
)

data class UserV1Address(
    val id: Long,
    val country: String,
    val region: String,
    val postCode: String,
    val city: String?,
    val street: String?,
    val building: String?,
    val apartment: String?
) {
    override fun toString() = StringBuilder().also { sb ->
        sb.append(country).append(",").append(region)
        city?.let { sb.append(",").append(it) }
        sb.append(",").append(postCode)
        street?.let { sb.append(",").append(it) }
        building?.let { sb.append(",").append(it) }
        apartment?.let { sb.append(",").append(it) }
    }.toString()
}

data class UserV1GenericMessage(
    val status: Int,
    val message: Any?,
    val error: String,
    val timestamp: String
)
