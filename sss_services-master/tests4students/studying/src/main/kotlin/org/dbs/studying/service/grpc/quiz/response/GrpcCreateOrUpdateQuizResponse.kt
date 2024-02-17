package org.dbs.quiz.service.grpc.quiz.response

import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.Patterns.QUIZ_QUESTION
import org.dbs.application.core.service.funcs.Patterns.QUIZ_RESPONSE
import org.dbs.application.core.service.funcs.Patterns.QUIZ_REQUEST_CODE
import org.dbs.application.core.service.funcs.Patterns.STR10N
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.entity.core.enums.ActionCodeEnum.AC_CREATE_OR_UPDATE_3S_QUIZ_REQUEST
import org.dbs.entity.core.enums.ActionCodeEnum.AC_CREATE_OR_UPDATE_3S_QUIZ_RESPONSE
import org.dbs.enums.I18NEnum.CUSTOMER_UNKNOWN_LOGIN
import org.dbs.enums.I18NEnum.FLD_INVALID_QUIZ_REQUEST_CODE
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmInTransaction
import org.dbs.ext.GrpcFuncs.fmRab
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginList
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.protobuf.quiz.QuizResponseDto
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.dto.quiz.response.QuizRequestInfoDb
import org.dbs.quiz.model.QuizResponse
import org.dbs.quiz.model.Template
import org.dbs.quiz.service.grpc.QuizGrpcService
import org.dbs.service.GrpcResponse
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.S3_QUIZ_REQUEST_CODE
import org.dbs.validator.Field.S3_QUIZ_RESPONSE_CODE
import org.dbs.validator.Field.S3_QUIZ_RESPONSE_QUESTION
import org.dbs.validator.Field.S3_QUIZ_RESPONSE_QUESTION_NUM
import org.dbs.validator.Field.S3_QUIZ_RESPONSE_QUESTION_RESP
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import org.dbs.protobuf.quiz.CreateOrUpdateQuizResponseRequest as REQ
import org.dbs.protobuf.quiz.CreateQuizResponseResponse as RESP
import org.dbs.protobuf.quiz.CreatedQuizResponseDto as ENT

object GrpcCreateOrUpdateQuizResponse {

    suspend fun QuizGrpcService.createOrUpdateQuizResponseInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run dto@{

        validateRemoteAddress(remoteAddress)
        val entityBuilder: ENT.Builder by lazy { ENT.newBuilder() }
        val grpcResponse: GrpcResponse<RESP> by lazy { { RESP.newBuilder().setResponseAnswer(it).build() } }

        buildGrpcResponse({
            it.run {
                val quizResponsesDb by lazy { CollectionLateInitVal<QuizResponse>() }
                val newQuizResponse = createCollection<Mono<QuizResponse>>()
                val updatedQuizResponse = createCollection<QuizResponse>()
                val modifiedQuizResponses = createCollection<QuizResponse>()
                val quizResponsesDto by lazy { CollectionLateInitVal<QuizResponseDto>() }

                val quizReqCode by lazy { LateInitVal<QuizRequestCode>() }
                val quizRequestInfo by lazy { LateInitVal<QuizRequestInfoDb>() }

                //==================================================================================================

                fun validateRequestData(): Boolean = run {

                    validateMandatoryField(quizRequestCode, QUIZ_REQUEST_CODE, S3_QUIZ_REQUEST_CODE)
                    { code ->
                        quizReqCode.hold(code)
                    }

                    if (quizResponsesList.isEmpty()) {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            S3_QUIZ_RESPONSE_CODE,
                            findI18nMessage(CUSTOMER_UNKNOWN_LOGIN)
                        )
                    } else {
                        quizResponsesList.forEach { resp ->
                            validateMandatoryField(resp.questionNum.toString(), STR10N, S3_QUIZ_RESPONSE_QUESTION_NUM)
                            validateMandatoryField(resp.questionWithAnswers.toString(), QUIZ_QUESTION, S3_QUIZ_RESPONSE_QUESTION)

                            resp.actualResponsesList.forEach { actResp ->
                                validateMandatoryField(actResp, QUIZ_RESPONSE, S3_QUIZ_RESPONSE_QUESTION_RESP)
                            }
                            resp.actualResponsesList.forEach { resps ->
                                validateMandatoryField(resps, QUIZ_RESPONSE, S3_QUIZ_RESPONSE_QUESTION_RESP)
                            }
                        }
                        quizResponsesDto.addAll(quizResponsesList)
                    }

                    noErrors()
                }

                fun findQuizRequestInfo() = fmStart {
                    quizRequestService.findInfo4QuizResponse(quizReqCode.value)
                        .switchIfEmpty {
                            addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                S3_QUIZ_REQUEST_CODE,
                                "${findI18nMessage(FLD_INVALID_QUIZ_REQUEST_CODE)} '${quizReqCode.value}'"
                            )
                            empty()
                        }
                        .map { info -> quizRequestInfo.hold(info); info }
                }

                fun MonoRAB.findQuizResponses() = fmRab {
                    quizResponseService.findByRequestCode(quizReqCode.value)
                        .collectList()
                        .map { quizResponses ->
                            quizResponsesDb.hold(quizResponses)
                        }
                }

                fun MonoRAB.updateOrCreateQuizResponse() = fmRab {
                     quizResponsesDto.value.forEach { quizRespDto ->
                        quizResponsesDb.value.firstOrNull { qzDb -> qzDb.questionNum == quizRespDto.questionNum }
                            ?.apply {
                                updatedQuizResponse.add(
                                    quizResponseService.quizResponseMappers.updateQuizResponse(
                                        this,
                                        request,
                                        quizRequestInfo.value
                                    )
                                )
                            } ?: run {
                                // TODO: change on createQuizResponse
//                            newQuizResponse.add(
//                                quizResponseService.createNewQuizRequest(
//                                    quizRequestService.generateQuizRequestCode(
//                                        customer.login,
//                                        openDateRes.value,
//                                        template.value.templateCode
//                                    ),
//                                    template.value.templateId,
//                                    customer.customerId
//                                ).flatMap { quizRequest ->
//                                    quizRequestService.quizRequestMappers.updateQuizRequest(
//                                        quizRequest,
//                                        request
//                                    ).toMono()
//                                }
//                            )
                        }
                    }
                    Flux.fromIterable(updatedQuizResponse).mergeWith(Flux.concat(newQuizResponse)).collectList()
                        .flatMap { quizRequests -> modifiedQuizResponses.addAll(quizRequests); quizRequests.toMono() }
                }

                suspend fun MonoRAB.save() = fmInTransaction {
                    r2dbcPersistenceService.executeAction(
                        modifiedQuizResponses,
                        AC_CREATE_OR_UPDATE_3S_QUIZ_RESPONSE,
                        remoteAddress,
                        "requestBodyString"
                    )
                }

                fun MonoRAB.finishResponseEntity() = fmFinish {
                    entityBuilder
                        // TODO: add codes
                        //.addAllQuizResponseCode()
                }

                if (validateRequestData()) {
                    processGrpcResponse {
                        findQuizRequestInfo()
                            .findQuizResponses()
                            .updateOrCreateQuizResponse()
                            .save()
                            .finishResponseEntity()
                    }
                }
                entityBuilder
            }
        })
        { grpcResponse(it) }
    }
}
