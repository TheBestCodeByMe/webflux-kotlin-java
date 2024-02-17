package org.dbs.auth.server

import org.dbs.consts.GrpcConsts.GRPC_SET
import org.dbs.consts.SecurityConsts.Cors.CORS_CONFIG_SET
import org.dbs.consts.SpringCoreConst.JWT_SET
import org.dbs.consts.SpringCoreConst.KAFKA_SET
import org.dbs.consts.SpringCoreConst.R2DBC_SET
import org.dbs.consts.SpringCoreConst.WEB_SET
import org.dbs.consts.SysConst.ALL_PACKAGES
import org.dbs.spring.boot.api.AbstractSpringBootApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = [ALL_PACKAGES])
class RAuthServer : AbstractSpringBootApplication() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runSpringBootApplication(args, RAuthServer::class.java)
        { JWT_SET + CORS_CONFIG_SET + GRPC_SET + WEB_SET + R2DBC_SET + KAFKA_SET }
    }
}
