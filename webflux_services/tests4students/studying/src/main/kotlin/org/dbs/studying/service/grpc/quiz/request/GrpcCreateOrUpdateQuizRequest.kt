package org.dbs.quiz.service.grpc.quiz.request

import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.LongFuncs.toLocalDateTime
import org.dbs.application.core.service.funcs.Patterns.LOGIN_PATTERN_MASK
import org.dbs.application.core.service.funcs.Patterns.QUIZ_TEMPLATE_CODE
import org.dbs.application.core.service.funcs.Patterns.STR100N
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.CustomerId
import org.dbs.consts.GrpcConsts.ContextKeys.CK_REMOTE_ADDRESS
import org.dbs.consts.IpAddress
import org.dbs.entity.core.enums.ActionCodeEnum.AC_CREATE_OR_UPDATE_3S_QUIZ_REQUEST
import org.dbs.enums.I18NEnum.CUSTOMER_UNKNOWN_LOGIN
import org.dbs.enums.I18NEnum.UNKNOWN_TEMPLATE_CODE
import org.dbs.ext.FluxFuncs.flatMapSuspend
import org.dbs.ext.GrpcFuncs.fmFinish
import org.dbs.ext.GrpcFuncs.fmInTransaction
import org.dbs.ext.GrpcFuncs.fmRab
import org.dbs.ext.GrpcFuncs.fmStart
import org.dbs.grpc.ext.GrpcNull.grpcGetOrNull
import org.dbs.grpc.ext.ResponseAnswer.noErrors
import org.dbs.grpc.ext.ResponseAnswer.unpackResponseEntity
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginDto
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginList
import org.dbs.protobuf.core.ResponseCode.RC_INVALID_REQUEST_DATA
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.model.Template
import org.dbs.quiz.service.grpc.QuizGrpcService
import org.dbs.service.GrpcResponse
import org.dbs.service.I18NService.Companion.findI18nMessage
import org.dbs.service.MonoRAB
import org.dbs.service.validator.GrpcProcessor.processGrpcResponse
import org.dbs.service.validator.GrpcValidators.addErrorInfo
import org.dbs.service.validator.GrpcValidators.validateMandatoryField
import org.dbs.service.validator.GrpcValidators.validateParamPatternIfPresent
import org.dbs.validator.Error.INVALID_ENTITY_ATTR
import org.dbs.validator.Field.S3_QUIZ_REQUEST_DEADLINE_DATE
import org.dbs.validator.Field.S3_QUIZ_REQUEST_FINISH_DATE
import org.dbs.validator.Field.S3_QUIZ_REQUEST_OPEN_DATE
import org.dbs.validator.Field.S3_QUIZ_REQUEST_REMAIN_TIME
import org.dbs.validator.Field.S3_QUIZ_REQUEST_START_DATE
import org.dbs.validator.Field.S3_QUIZ_REQUEST_TIME_SPENT
import org.dbs.validator.Field.S3_QUIZ_TEMPLATE_CODE
import org.dbs.validator.Field.SOME_FLD_CUSTOMER_LOGIN
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime
import java.time.LocalTime
import org.dbs.protobuf.quiz.CreateOrUpdateQuizRequestRequest as REQ
import org.dbs.protobuf.quiz.CreateQuizRequestResponse as RESP
import org.dbs.protobuf.quiz.CreatedQuizRequestDto as ENT

object GrpcCreateOrUpdateQuizRequest {

    suspend fun QuizGrpcService.createOrUpdateQuizRequestInternal(
        request: REQ,
        remoteAddress: IpAddress = CK_REMOTE_ADDRESS.get(),
    ): RESP = request.run dto@{

        validateRemoteAddress(remoteAddress)
        val entityBuilder: ENT.Builder by lazy { ENT.newBuilder() }
        val grpcResponse: GrpcResponse<RESP> by lazy { { RESP.newBuilder().setResponseAnswer(it).build() } }

        buildGrpcResponse({
            it.run {
                val template by lazy { LateInitVal<Template>() }

                val quizRequestsDb by lazy { CollectionLateInitVal<QuizRequest>() }
                val newQuizRequest = createCollection<Mono<QuizRequest>>()
                val updatedQuizRequest = createCollection<QuizRequest>()
                val modifiedQuizRequests = createCollection<QuizRequest>()

                val tempCode by lazy { LateInitVal<String>() }

                val customerIds = createCollection<CustomerId>()
                val customerDtos by lazy { CollectionLateInitVal<GetSchoolCustomerByLoginDto>() }
                val customerLogins = StringBuilder()

                val timeSpentRes by lazy { LateInitVal<LocalTime>() }
                val remainTimeRes by lazy { LateInitVal<LocalTime>() }
                val openDateRes by lazy { LateInitVal<LocalDateTime>() }
                val deadlineDateRes by lazy { LateInitVal<LocalDateTime>() }
                val startDateRes by lazy { LateInitVal<LocalDateTime>() }
                val finishDateRes by lazy { LateInitVal<LocalDateTime>() }
                //==================================================================================================

                fun validateRequestData(): Boolean = run {

                    validateMandatoryField(templateCode, QUIZ_TEMPLATE_CODE, S3_QUIZ_TEMPLATE_CODE)
                    { code ->
                        tempCode.hold(code)
                    }

                    if (loginsList.isEmpty()) {
                        addErrorInfo(
                            RC_INVALID_REQUEST_DATA,
                            INVALID_ENTITY_ATTR,
                            SOME_FLD_CUSTOMER_LOGIN,
                            findI18nMessage(CUSTOMER_UNKNOWN_LOGIN)
                        )
                    }

                    loginsList.forEach { login ->
                        validateMandatoryField(
                            login,
                            LOGIN_PATTERN_MASK, SOME_FLD_CUSTOMER_LOGIN
                        ) { custLogin ->
                            customerLogins.append("${custLogin},")
                        }
                    }

                    validateMandatoryField(timeSpent.toString(), STR100N, S3_QUIZ_REQUEST_TIME_SPENT)
                    { code ->
                        timeSpentRes.hold(code.toLong().toLocalDateTime().toLocalTime())
                    }

                    validateMandatoryField(openDate.toString(), STR100N, S3_QUIZ_REQUEST_OPEN_DATE)
                    { code ->
                        openDateRes.hold(code.toLong().toLocalDateTime())
                    }

                    validateMandatoryField(deadlineDate.toString(), STR100N, S3_QUIZ_REQUEST_DEADLINE_DATE)
                    { code ->
                        deadlineDateRes.hold(code.toLong().toLocalDateTime())
                    }

                    validateParamPatternIfPresent(startDate.toString().grpcGetOrNull(), STR100N, S3_QUIZ_REQUEST_START_DATE)
                    { code ->
                        startDateRes.hold(code.toLong().toLocalDateTime())
                    }

                    validateParamPatternIfPresent(finishDate.toString().grpcGetOrNull(), STR100N, S3_QUIZ_REQUEST_FINISH_DATE)
                    { code ->
                        finishDateRes.hold(code.toLong().toLocalDateTime())
                    }

                    validateParamPatternIfPresent(remainTime.toString().grpcGetOrNull(), STR100N, S3_QUIZ_REQUEST_REMAIN_TIME)
                    { code ->
                        remainTimeRes.hold(code.toLong().toLocalDateTime().toLocalTime())
                    }

                    noErrors()
                }

                fun findTemplate() = fmStart {
                    templateService.findTemplateByCode(tempCode.value)
                        .switchIfEmpty {
                            addErrorInfo(
                                RC_INVALID_REQUEST_DATA,
                                INVALID_ENTITY_ATTR,
                                S3_QUIZ_TEMPLATE_CODE,
                                "${findI18nMessage(UNKNOWN_TEMPLATE_CODE)} '${tempCode.value}'"
                            )
                            empty()
                        }
                        .map { temp -> template.hold(temp); temp }
                }

                suspend fun MonoRAB.findCustomerIds() = flatMapSuspend {
                    actorsClientService.getSchoolCustomerByLogins(removeDelimiter(customerLogins).toString()).run {
                        if (responseAnswer.hasErrorMessage()) {
                            addAllErrorMessages(responseAnswer.errorMessagesList)
                        } else {
                            with(responseAnswer.unpackResponseEntity<GetSchoolCustomerByLoginList>()) {
                                customerDtos.addAll(customersList)
                            }
                        }
                        it.takeIf { it.noErrors() }.toMono()
                    }
                }

                fun MonoRAB.findQuizRequests() = fmRab {
                    customerDtos.value.forEach { customer -> customerIds.add(customer.customerId) }

                    quizRequestService.findByCustomerIdsAndTemplateId(customerIds, template.value.templateId)
                        .collectList()
                        .map { quizRequests ->
                            quizRequestsDb.hold(quizRequests)
                        }
                }

                fun MonoRAB.updateOrCreateQuizRequest() = fmRab {
                    customerDtos.value.forEach { customer ->
                        quizRequestsDb.value.firstOrNull { qzDb -> qzDb.custId == customer.customerId }
                            ?.apply {
                                updatedQuizRequest.add(
                                    quizRequestService.quizRequestMappers.updateQuizRequest(
                                        this,
                                        request
                                    )
                                )
                            } ?: run {
                            newQuizRequest.add(
                                quizRequestService.createNewQuizRequest(
                                    quizRequestService.generateQuizRequestCode(
                                        customer.login,
                                        openDateRes.value,
                                        template.value.templateCode
                                    ),
                                    template.value.templateId,
                                    customer.customerId
                                ).flatMap { quizRequest ->
                                    quizRequestService.quizRequestMappers.updateQuizRequest(
                                        quizRequest,
                                        request
                                    ).toMono()
                                }
                            )
                        }
                    }
                    Flux.fromIterable(updatedQuizRequest).mergeWith(Flux.concat(newQuizRequest)).collectList()
                        .flatMap { quizRequests -> modifiedQuizRequests.addAll(quizRequests); quizRequests.toMono() }
                }

                suspend fun MonoRAB.save() = fmInTransaction {
                    r2dbcPersistenceService.executeAction(
                        modifiedQuizRequests,
                        AC_CREATE_OR_UPDATE_3S_QUIZ_REQUEST,
                        remoteAddress,
                        "requestBodyString"
                    )
                }

                fun MonoRAB.finishResponseEntity() = fmFinish {
                    entityBuilder
                        .setTemplateCode(tempCode.value)
                        .addAllLogins(loginsList)
                }

                if (validateRequestData()) {
                    processGrpcResponse {
                        findTemplate()
                            .findCustomerIds()
                            .findQuizRequests()
                            .updateOrCreateQuizRequest()
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
