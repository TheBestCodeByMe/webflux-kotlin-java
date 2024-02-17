package org.dbs.media.rest

import org.dbs.consts.RestHttpConsts.RestQueryParams.File.QP_BUCKET_NAME
import org.dbs.consts.RestHttpConsts.RestQueryParams.File.QP_FILE_NAME
import org.dbs.media.service.MediaService
import org.dbs.rest.api.ResponseBody
import org.dbs.rest.service.AbstractMonoRestProcessor
import org.dbs.spring.core.api.EntityInfo
import org.dbs.store.dto.CreatedMediaFileResponse
import org.dbs.store.enums.MediaBucketEnum.DEFAULT
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.IMAGE_JPEG
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class MediaRest(private val mediaService: MediaService) : AbstractMonoRestProcessor() {
    fun uploadFile(serverRequest: ServerRequest) =
        createResponse(
            serverRequest,
            CreatedMediaFileResponse::class.java,
            mediaService::uploadFileAndReturnReference
        )

    fun downloadFile(request: ServerRequest): Mono<ServerResponse> =
        request.toMono()
            .map { download(request) }
            .flatMap { getFileResponse(it) }
            .onErrorResume { getErrorFileResponse(it) }

    private fun getFileResponse(inputStreamResource: InputStreamResource) = ServerResponse
        .status(OK)
        .contentType(IMAGE_JPEG)
        .bodyValue(inputStreamResource)

    private fun getErrorFileResponse(throwable: Throwable) =
        ServerResponse.status(INTERNAL_SERVER_ERROR)
            .contentType(APPLICATION_JSON)
            .bodyValue(ResponseBody<EntityInfo>().also { it.error = throwable.message })

    private fun download(request: ServerRequest) =
        mediaService.downloadFile(
            request.queryParam(QP_FILE_NAME).orElseThrow(),
            request.queryParam(QP_BUCKET_NAME).orElse(DEFAULT.bucketName)
        )
}
