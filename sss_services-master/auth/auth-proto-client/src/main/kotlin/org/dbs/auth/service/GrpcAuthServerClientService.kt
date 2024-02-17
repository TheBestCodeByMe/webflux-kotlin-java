package org.dbs.auth.service

import org.dbs.consts.SpringCoreConst.PropertiesNames.AUTH_SERVER_SERVICE_HOST
import org.dbs.consts.SpringCoreConst.PropertiesNames.AUTH_SERVER_SERVICE_PORT
import org.dbs.protobuf.auth.AuthServerClientServiceGrpcKt
import org.dbs.consts.RestHttpConsts.URI_LOCALHOST_DOMAIN
import org.dbs.service.AbstractGrpcClientService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GrpcAuthServerClientService(
    @Value("\${$AUTH_SERVER_SERVICE_HOST:$URI_LOCALHOST_DOMAIN}")
    private val grpcUrl: String,
    @Value("\${$AUTH_SERVER_SERVICE_PORT:6454}")
    private val grpcPort: Int
) : AbstractGrpcClientService<AuthServerClientServiceGrpcKt.AuthServerClientServiceCoroutineStub>(grpcUrl, grpcPort)
