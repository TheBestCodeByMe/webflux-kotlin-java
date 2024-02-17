package org.dbs.auth.server.clients.v1.client

import org.dbs.consts.SpringCoreConst.EMPTY_HTTP_HEADERS
import org.dbs.spring.core.api.AbstractWebClientService
import org.dbs.validator.Error.USER_DOES_NOT_EXISTS
import org.dbs.validator.Field.SSS_USER_LOGIN
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.consts.AuthServerConsts.V1.ROUTE_USER_V1_SIGN
import org.dbs.auth.server.v1.dto.UserV1DtoResponse
import org.dbs.auth.server.v1.dto.UserV1GenericMessage
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_LOGIN
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_PASSWORD
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_RESPONSE_DATA
import org.dbs.service.RAB
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty

/**
 * Monolith rest client
 */
@Service
class V1Client : AbstractWebClientService() {

    @Value("\${config.restful.security.some-v1.server:https://localhost}")
    private val mainServer = EMPTY_STRING

    override fun initialize() = super.initialize().also { prepareDefaultWebClient(mainServer) }

    fun getUserV1Credentials(userV1Login: String, userV1Password: String, rab: RAB): Mono<UserV1DtoResponse> =
        webClientExecute(
            ROUTE_USER_V1_SIGN,
            EMPTY_STRING,
            UserV1DtoResponse::class.java,
            {
                it.queryParam(QP_LOGIN, userV1Login)
                    .queryParam(QP_PASSWORD, userV1Password)
            },
            EMPTY_HTTP_HEADERS
        )
        {
            it.bodyToMono(UserV1GenericMessage::class.java)
                .subscribe {
                    rab.addErrorInfo(
                        RC_INVALID_RESPONSE_DATA,
                        USER_DOES_NOT_EXISTS,
                        SSS_USER_LOGIN,
                        it.error
                    )
                }
        }
            .also {
            logger.debug("execute query to $mainServer$ROUTE_USER_V1_SIGN")
            }.onErrorResume { empty() }

    fun toJwtParams(userV1Response: UserV1DtoResponse) = userV1Response.run {
        JwtParamsDto(
            userId = message.user.id,
            email = message.user.email,
            address = message.user.address.map { it.toString() }.firstOrNull(),
            roles = message.user.roles.joinToString { it },
            userLogin = message.user.login,
            firstName = message.user.firstName,
            lastName = message.user.lastName,
            phone = message.user.phone
        )
    }
}
