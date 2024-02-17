package org.dbs.media.service


import org.dbs.application.core.api.LateInitVal
import org.dbs.consts.RestHttpConsts.RestQueryParams.File.MULTIPART_PARAM_BUCKET_NAME
import org.dbs.consts.RestHttpConsts.RestQueryParams.File.MULTIPART_PARAM_FILE
import org.dbs.consts.RestHttpConsts.RestQueryParams.File.MULTIPART_PARAM_PREVIOUS_FILE_NAME
import org.dbs.rest.api.enums.RestOperCodeEnum.OC_OK
import org.dbs.rest.service.MonoRestApplicationService
import org.dbs.media.exception.MultipartParamFileException
import org.dbs.store.dto.CreatedMediaFile
import org.dbs.store.dto.CreatedMediaFileResponse
import org.dbs.store.dto.UploadFileDto
import org.dbs.store.enums.MediaBucketEnum.DEFAULT
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.codec.multipart.FormFieldPart
import org.springframework.http.codec.multipart.Part
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.fromCallable
import reactor.core.scheduler.Schedulers.boundedElastic
import reactor.kotlin.core.publisher.toMono
import java.io.File
import java.io.File.createTempFile

@Service
class MediaService(private val minioService: MinioService) : MonoRestApplicationService() {

    fun uploadFileAndReturnReference(serverRequest: ServerRequest): Mono<CreatedMediaFileResponse> = run {
        val uploadFileDto = LateInitVal<UploadFileDto>()
        serverRequest.body(BodyExtractors.toMultipartData())
            .flatMap(::getMapValue)
            .map { uploadFileDto.hold(it); it.filePart }
            .flatMap(::createTmpFile)
            .flatMap { fileTmp -> uploadFileToMono(fileTmp, uploadFileDto.value.bucketName) }
            .doOnNext { removePreviousFile(uploadFileDto.value) }
            .map { path ->
                CreatedMediaFileResponse().apply {
                    createdEntity = CreatedMediaFile(path)
                    code = OC_OK
                    message = "File is uploaded (${uploadFileDto.value.bucketName})"
                }
            }
    }

    private fun getBucketName(map: Map<String, Part>) =
        map[MULTIPART_PARAM_BUCKET_NAME]?.let {
            (it as FormFieldPart).value()
        } ?: DEFAULT.bucketName

    private fun getPreviousFileName(map: Map<String, Part>) =
        map[MULTIPART_PARAM_PREVIOUS_FILE_NAME]?.let {
            (it as FormFieldPart).value()
        }

    private fun getFilePart(map: Map<String, Part>) =
        (map[MULTIPART_PARAM_FILE]?.let { it as FilePart }
            ?: throw MultipartParamFileException("multipart param file is empty"))

    fun downloadFile(fileName: String, bucketName: String) = minioService.downloadFile(fileName, bucketName)

    private fun getMapValue(parts: MultiValueMap<String, Part>): Mono<UploadFileDto> =
        parts.run {
            val map: Map<String, Part> = toSingleValueMap()
            UploadFileDto(
                getBucketName(map),
                getFilePart(map),
                getPreviousFileName(map)
            ).toMono()
        }

    private fun createTmpFile(filePart: FilePart): Mono<File> =
        filePart.run {
            fromCallable { createTempFile("file", filename()) }
                .subscribeOn(boundedElastic())
                .flatMap { transferTo(it).then(it.toMono()) }
        }

    private fun uploadFileToMono(file: File, bucketName: String): Mono<String> =
        file.run {
            fromCallable {
                minioService.uploadFile(name, absolutePath, bucketName)
            }.subscribeOn(boundedElastic())
        }

    private fun removePreviousFile(uploadFileDto: UploadFileDto) {
        uploadFileDto.previousFileName?.let { fileNameForRemove ->
            minioService.removeFile(fileNameForRemove, uploadFileDto.bucketName)
        }
    }
}
