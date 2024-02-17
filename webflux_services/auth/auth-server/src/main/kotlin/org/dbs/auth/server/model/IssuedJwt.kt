package org.dbs.auth.server.model

import org.dbs.auth.server.enums.ApplicationEnum
import org.dbs.consts.Jwt
import org.dbs.consts.JwtId
import org.dbs.consts.OperDate
import org.dbs.consts.OperDateNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tkn_issued_jwt")
data class IssuedJwt(
    @Id
    @Column("jwt_id")
    val jwtId: JwtId,

    @Column("issue_date")
    val issueDate: OperDate,

    @Column("valid_until")
    val validUntil: OperDate,

    @Column("jwt")
    val jwt: Jwt,

    @Column("application_id")
    val applicationId: ApplicationEnum,

    @Column("issued_to")
    val issuedTo: String,

    @Column("is_revoked")
    val isRevoked: Boolean,

    @Column("revoke_date")
    val revokeDate: OperDateNull,

    @Column("tag")
    val tag: String

) : AbstractJwt() {
    override fun getId(): JwtId = jwtId
}
