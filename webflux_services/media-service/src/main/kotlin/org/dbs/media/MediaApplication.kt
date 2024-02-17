package org.dbs.media

import org.dbs.consts.SecurityConsts.Cors.CORS_CONFIG_SET
import org.dbs.consts.SpringCoreConst.MEDIA_SET
import org.dbs.consts.SpringCoreConst.WEB_SET
import org.dbs.spring.boot.api.AbstractSpringBootApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, R2dbcAutoConfiguration::class])
class MediaApplication : AbstractSpringBootApplication() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runSpringBootApplication(args, MediaApplication::class.java)
        { WEB_SET + CORS_CONFIG_SET + MEDIA_SET }
    }
}
