package org.dbs.auth.server.v1.validator

import org.dbs.ext.CollectionFuncs.whenNoErrors
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.GENERAL_FIELD
import org.dbs.validator.Field.SSS_LOGIN_USER
import org.dbs.validator.Field.SSS_VENDOR_LOGIN
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.v1.UserV1Login
import org.dbs.auth.server.v1.dto.UserV1DtoResponse
import org.dbs.auth.server.v1.stream.UserV1LoginStreamProcessor
import org.dbs.consts.RestHttpConsts.Exceptions.EX_BAD_REQUEST
import org.dbs.consts.RestHttpConsts.Exceptions.EX_NOT_FOUND
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_ID
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_LOGIN
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_USER_LOGIN_OR_PASSWORD
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_INVALID_VENDOR_LOGIN_OR_PASSWORD
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNAUTHORIZED_USER
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_UNEXPECTED_EXCEPTION
import org.dbs.rest.api.validator.AbstractStreamValidatorService
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.springframework.http.HttpStatus.OK
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Deprecated("remove")
data class UserV1LoginValidator(
    private val userV1Login: UserV1Login,
) : AbstractStreamValidatorService<UserV1LoginStreamProcessor>() {

    override fun validate(
        serverRequest: ServerRequest,
        streamProcessor: UserV1LoginStreamProcessor
    ): Mono<UserV1LoginStreamProcessor> =
        streamProcessor.run {
            ip.hold( serverRequest.ip())
            with(requestBody.entityInfo) {
                // validate login
                userLogin?.let { spUserLogin.hold(it) } ?: run {
                    responseBody.code = OC_INVALID_VENDOR_LOGIN_OR_PASSWORD
                    addErrorInfo(
                        OC_INVALID_VENDOR_LOGIN_OR_PASSWORD,
                        INVALID_ENTITY_ATTR,
                        SSS_VENDOR_LOGIN,
                        "user login not specified"
                    )
                }

                // validate password
                userPass?.let { spUserPass.hold(it) } ?: run {
                    responseBody.code = OC_INVALID_VENDOR_LOGIN_OR_PASSWORD
                    addErrorInfo(
                        OC_INVALID_VENDOR_LOGIN_OR_PASSWORD,
                        INVALID_ENTITY_ATTR,
                        SSS_VENDOR_LOGIN,
                        "user pass not specified"
                    )
                }
            }
            responseBody.errors.whenNoErrors {
                //logger.debug("getting userV1 info: {}", spUserLogin)
                userV1Login.findUserV1Login(spUserLogin.value, spUserPass.value)
                    .doOnError { throwable -> registerException(throwable, this) }
                    .onErrorResume { empty() }
                    .flatMap { response -> processResponse(this, response) }
                    .switchIfEmpty { registerDefaultException(this, spUserLogin.value) }
            }
        }

    private fun processResponse(sp: UserV1LoginStreamProcessor, response: UserV1DtoResponse) = sp.run {
        logger.debug { "process userV1 response: [$response]" }
        if (response.status == OK.value()) {
            // status user
            if (!response.message.user.enabled) {
                responseBody.apply {
                    code = OC_UNAUTHORIZED_USER
                    addErrorInfo(
                        OC_UNAUTHORIZED_USER,
                        INVALID_ENTITY_ATTR,
                        SSS_VENDOR_LOGIN,
                        "login '${response.message.user.login}' is disabled"
                    )
                }
            }
            // userId
            if (response.message.user.id <= 0) {
                responseBody.apply {
                    code = OC_INVALID_USER_ID
                    addErrorInfo(
                        OC_INVALID_USER_ID,
                        INVALID_ENTITY_ATTR,
                        SSS_LOGIN_USER,
                        "invalid userId - '${response.message.user.id}'"
                    )
                }
            }
            if (responseBody.errors.isEmpty()) {
                sp.userV1Dto.hold(response)
                sp.jwtParams.hold(toJwtParams(response))
            }
        } else {
            //other error
            responseBody.apply {
                code = OC_UNEXPECTED_EXCEPTION
                addErrorInfo(OC_UNEXPECTED_EXCEPTION, INVALID_ENTITY_ATTR, SSS_LOGIN_USER, response.error)
            }
        }
        if (responseBody.errors.isNotEmpty()) empty() else toMono()
    }

    private fun toJwtParams(userV1Response: UserV1DtoResponse) = userV1Response.run {
        JwtParamsDto(
            userId = message.user.id,
            userLogin = message.user.login,
            firstName = message.user.firstName,
            lastName = message.user.lastName,
            phone = message.user.phone,
            email = message.user.email,
            address = message.user.address.map { it.toString() }.firstOrNull(),
            someId = message.someId?.toString(),
            roles = message.user.roles.joinToString { it },
            someTimeZone = message.someTimeZone
        )
    }

    private fun registerDefaultException(sp: UserV1LoginStreamProcessor, spUserLogin: String)
            : Mono<UserV1LoginStreamProcessor> =
        sp.responseBody.run {
            if (errors.isEmpty()) {
                code = OC_INVALID_USER_LOGIN
                addErrorInfo(
                    OC_INVALID_USER_LOGIN,
                    INVALID_ENTITY_ATTR,
                    SSS_LOGIN_USER,
                    "unknown user login '($spUserLogin)' or invalid password"
                )
            }
            empty()
        }

    private fun registerException(throwable: Throwable, sp: UserV1LoginStreamProcessor) {
        val exceptionSimpleClassName = throwable.javaClass.simpleName
        sp.apply {
            when (exceptionSimpleClassName) {
                EX_NOT_FOUND, EX_BAD_REQUEST -> {
                    responseBody.code = OC_INVALID_USER_LOGIN_OR_PASSWORD
                    addErrorInfo(
                        OC_INVALID_USER_LOGIN_OR_PASSWORD,
                        INVALID_ENTITY_ATTR,
                        SSS_LOGIN_USER,
                        "invalid user login or password"
                    )
                }

                else -> {
                    logger.error("error", throwable)
                    responseBody.code = OC_UNEXPECTED_EXCEPTION
                    addErrorInfo(
                        OC_UNEXPECTED_EXCEPTION,
                        INVALID_ENTITY_ATTR,
                        GENERAL_FIELD,
                        throwable.message ?: throwable.javaClass.canonicalName
                    )
                }
            }
        }
    }
}
