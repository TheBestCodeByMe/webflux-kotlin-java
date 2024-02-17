package org.dbs.auth.server.enums

import org.dbs.auth.server.consts.ApplicationId
import org.dbs.consts.ApplicationName
import org.dbs.consts.SysConst.APP_ACTORS
import org.dbs.consts.SysConst.APP_BANKING_CORE
import org.dbs.consts.SysConst.SOME_APP_SOME
import org.dbs.consts.SysConst.SOME_APP_V1
import org.dbs.exception.UnknownEnumException

enum class ApplicationEnum(
    private val applicationId: ApplicationId,
    private val applicationName: ApplicationName
) {
    USER(100, SOME_APP_V1);

    companion object {
        fun getEnum(applicationId: Int): ApplicationEnum =
            entries.find { it.applicationId == applicationId }
                ?: throw UnknownEnumException("applicationId = $applicationId")

        fun isExistEnum(id: Int) = entries.find { it.getApplicationId() == id }?.let { true } ?: false
    }

    fun getApplicationName() = this.applicationName
    fun getApplicationId() = this.applicationId
}
