package org.dbs.media.config

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.dbs.consts.RestHttpConsts.HTTP_200_STRING
import org.dbs.consts.RestHttpConsts.RestQueryParams.File.QP_FILE_NAME
import org.dbs.consts.RestHttpConsts.RestQueryParams.QP_STRING_TYPE
import org.dbs.consts.SysConst
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.RouteTags.ROUTE_TAG_MEDIA_FILE
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.Routes.ROUTE_DOWNLOAD_MEDIA_FILE
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.Routes.ROUTE_UPLOAD_MEDIA_FILE
import org.dbs.store.dto.CreatedMediaFileResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.core.io.InputStreamResource
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.reactive.function.server.ServerRequest

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@RouterOperations(
    RouterOperation(
        path = ROUTE_DOWNLOAD_MEDIA_FILE,
        method = [GET],
        operation = Operation(
            description = "Download media file",
            tags = [ROUTE_TAG_MEDIA_FILE],
            operationId = ROUTE_DOWNLOAD_MEDIA_FILE,
            parameters = [Parameter(
                `in` = QUERY,
                name = QP_FILE_NAME,
                schema = Schema(type = QP_STRING_TYPE),
                description = "Unique filename",
                example = "someName"
            )],
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Media resource",
                    content = [Content(schema = Schema(implementation = InputStreamResource::class))]
                )
            ]
        )
    ),
    RouterOperation(
        path = ROUTE_UPLOAD_MEDIA_FILE,
        method = [POST],
        operation = Operation(
            description =
            """
                Upload Media file
            """,
            tags = [ROUTE_TAG_MEDIA_FILE],
            operationId = ROUTE_UPLOAD_MEDIA_FILE,
            requestBody = RequestBody(
                description = """Media file.""",
                content = [Content(schema = Schema(implementation = ServerRequest::class))]
            ),
            responses = [
                ApiResponse(
                    responseCode = HTTP_200_STRING,
                    description = "Upload media file",
                    content = [Content(schema = Schema(implementation = CreatedMediaFileResponse::class))]
                )
            ]
        )
    )
)
annotation class OpenApiRoutesDefinitions
