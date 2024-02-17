package org.dbs.auth.server.model

import org.dbs.auth.server.consts.ApplicationId
import org.dbs.consts.ApplicationName
import org.dbs.spring.ref.AbstractRefEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("tkn_applications")
data class Application(
    @Id
    @Column("application_id") val applicationId: ApplicationId,
    @Column("application_code") val applicationCode: String,
    @Column("application_name") val applicationName: ApplicationName
) : AbstractRefEntity<Int>() {
    override fun getId() = applicationId
}
