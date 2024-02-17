package org.dbs.auth.server.clients.v1.service

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.auth.server.consts.AuthServerConsts.URI.SOME_USERV1_LOGIN
import org.dbs.auth.server.consts.AuthServerConsts.URI.SOME_USERV1_REFRESH
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.SysConst.SOME_APP_V1
import org.dbs.rest.dto.jwt.CreateJwtRequest
import org.dbs.rest.dto.jwt.CreatedJwtResponse
import org.dbs.rest.dto.value.LoginUserRequest
import org.dbs.rest.dto.value.LoginUserResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.web.bind.annotation.RequestMethod.POST
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION


@Target(FUNCTION)
@Retention(RUNTIME)
@RouterOperations(
    RouterOperation(
        path = SOME_USERV1_LOGIN,
        method = [POST],
        operation = Operation(
            tags = [SOME_APP_V1],
            operationId = SOME_USERV1_LOGIN,
            requestBody = RequestBody(
                description = "UserV1 login request details",
                content = [Content(schema = Schema(implementation = LoginUserRequest::class))]
            ),
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "login user and create jwt token",
                content = [Content(schema = Schema(implementation = LoginUserResponse::class))]
            )]
        )
    ),
    RouterOperation(
        path = SOME_USERV1_REFRESH,
        method = [POST],
        operation = Operation(
            tags = [SOME_APP_V1],
            operationId = SOME_USERV1_REFRESH,
            requestBody = RequestBody(
                description = "UserV1 refresh jwt request details",
                content = [Content(schema = Schema(implementation = CreateJwtRequest::class))]
            ),
            responses = [ApiResponse(
                responseCode = HTTP_200_STRING,
                description = "UserV1 updated jwt response",
                content = [Content(schema = Schema(implementation = CreatedJwtResponse::class))]
            )]
        )
    )
)
annotation class OpenApiV1RoutesDefinitions
