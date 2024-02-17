package org.dbs.auth.server.service.grpc

import net.devh.boot.grpc.server.service.GrpcService
import org.dbs.auth.server.clients.actors.ActorsSecurityService
import org.dbs.auth.server.clients.actors.client.ActorsClientService
import org.dbs.auth.server.clients.actors.grpc.GrpcManagerLogin.loginManagerInternal
import org.dbs.auth.server.clients.actors.grpc.GrpcManagerRefreshJwt.managerRefreshJwtInternal
import org.dbs.auth.server.clients.v1.client.SchoolV1Client
import org.dbs.auth.server.clients.v1.grpc.GrpcUserV1Login.loginUserV1Internal
import org.dbs.auth.server.clients.v1.grpc.GrpcUserV1RefreshJwt.userV1RefreshJwtInternal
import org.dbs.auth.server.clients.v1.service.SmartSafeSchoolV1SecurityService
import org.dbs.auth.server.consts.GrpcConsts.CK_ACTOR_MANAGER_LOGIN_PROCEDURE
import org.dbs.auth.server.consts.GrpcConsts.CK_USER_V1_LOGIN_PROCEDURE
import org.dbs.protobuf.auth.AuthServerClientServiceGrpcKt
import org.dbs.protobuf.auth.ManagerLoginRequest
import org.dbs.protobuf.auth.ManagerLoginResponse
import org.dbs.protobuf.auth.RefreshManagerJwtRequest
import org.dbs.protobuf.auth.RefreshManagerJwtResponse
import org.dbs.protobuf.auth.RefreshUserV1JwtRequest
import org.dbs.protobuf.auth.RefreshUserV1JwtResponse
import org.dbs.protobuf.auth.UserV1LoginRequest
import org.dbs.protobuf.auth.UserV1LoginResponse
import org.dbs.service.AbstractGrpcServerService
import org.dbs.spring.core.api.PublicApplicationBean
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServerGrpcService(
    val passwordEncoder: PasswordEncoder,
    val securityService: SmartSafeSchoolV1SecurityService,
    val v1Client: SchoolV1Client
) : AbstractGrpcServerService(), PublicApplicationBean {

    // white grpc procedures
    override val whiteProcs = listOf(CK_USER_V1_LOGIN_PROCEDURE)

    @GrpcService
    inner class AuthServerService : AuthServerClientServiceGrpcKt.AuthServerClientServiceCoroutineImplBase(),
        PublicApplicationBean {

        override suspend fun userV1Login(request: UserV1LoginRequest): UserV1LoginResponse =
            loginUserV1Internal(request)

        override suspend fun refreshUserV1Jwt(request: RefreshUserV1JwtRequest): RefreshUserV1JwtResponse =
            userV1RefreshJwtInternal(request)

    }
}
