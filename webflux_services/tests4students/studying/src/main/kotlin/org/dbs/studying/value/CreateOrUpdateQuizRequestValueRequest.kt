package org.dbs.quiz.value

import org.dbs.grpc.api.H1h2
import org.dbs.quiz.service.QuizRequestService
import org.dbs.quiz.service.grpc.QuizGrpcService
import org.dbs.quiz.service.grpc.quiz.request.GrpcCreateOrUpdateQuizRequest.createOrUpdateQuizRequestInternal
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.id
import org.dbs.rest.service.ServerRequestFuncs.ip
import org.dbs.rest.service.value.GenericRequest
import org.dbs.rest.service.value.HttpReactivePostRequest
import org.dbs.spring.core.api.ServiceLocator.findService
import org.springframework.web.reactive.function.server.ServerRequest
import org.dbs.quiz.dto.quiz.request.CreateOrUpdateQuizRequestDto as DTO
import org.dbs.quiz.dto.quiz.request.CreateOrUpdateQuizRequestRequest as H1_REQ
import org.dbs.quiz.dto.quiz.request.CreateQuizRequestResponse as H1_RES
import org.dbs.quiz.dto.quiz.request.CreatedQuizRequest as OUT_DTO
import org.dbs.quiz.service.grpc.h1.convert.quiz.request.CreateOrUpdateQuizRequestConverter as H1_CONV

@JvmInline
value class CreateOrUpdateQuizRequestValueRequest<R : ServerRequest>(private val serverRequest: R) :
    GenericRequest<R>, HttpReactivePostRequest<DTO, H1_REQ, OUT_DTO, H1_RES> {
    suspend fun buildResponse(processor: H1_PROCESSOR<R>, quizRequestService: QuizRequestService) =
        processor.createResponseCo(
            serverRequest,
            H1_REQ::class.java,
            H1_RES::class.java
        ) {
            serverRequest.run {
                quizRequestService.buildMonoResponse(this, H1_REQ::class.java)
                {
                    H1h2.applyH1Converter(H1_CONV::class, it, id())
                    { with(actorGrpcService) { createOrUpdateQuizRequestInternal(it, ip()) } }
                }
            }
        }

    companion object {
        val actorGrpcService by lazy { findService(QuizGrpcService::class.java) }
    }
}
