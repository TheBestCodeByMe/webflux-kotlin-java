package com.ulia.quiz.request

import api.TestConst
import api.TestConst.REPEATED_KOTEST_AMOUNT
import api.TestConst.TEMPLATE_TEST_AMOUNT
import com.ulia.quiz.testfuncs.AbstractQuizTest
import com.ulia.quiz.testfuncs.QuizRequestFuncs.createOrUpdateTestQuizRequest
import com.ulia.quiz.testfuncs.QuizRequestFuncs.createTestQuizRequest
import com.ulia.quiz.testfuncs.QuizRequestFuncs.quizRequestCount
import com.ulia.quiz.testfuncs.QuizRequestFuncs.updateStatusTestQuizRequest
import com.ulia.quiz.testfuncs.QuizRequestFuncs.updateStatusTestStartQuizRequest
import com.ulia.quiz.testfuncs.QuizTemplateFuncs.createTestQuizTemplate
import com.ulia.quiz.testfuncs.QuizTemplateFuncs.tempatesCount
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.logging.log4j.kotlin.logger
import org.dbs.application.core.service.funcs.ServiceFuncs
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.application.core.service.funcs.TestFuncs.generateTestString
import org.dbs.consts.Login
import org.dbs.entity.core.enums.EntityStatusEnum
import org.dbs.entity.core.enums.EntityStatusEnum.S3_QUIZ_REQUEST_STATUS_CLOSED
import org.dbs.entity.core.enums.EntityStatusEnum.S3_QUIZ_REQUEST_STATUS_IN_PROGRESS
import org.dbs.quiz.consts.QuizRequestCode
import org.dbs.quiz.model.QuizRequest
import org.dbs.quiz.model.Template
import org.dbs.quiz.service.grpc.actors.client.ActorsClientService
import org.dbs.rest.api.enums.RestOperCodeEnum
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

typealias MonoQuizRequest = Mono<QuizRequest>
typealias MonoTemplate = Mono<Template>

class QuizRequestTests : AbstractQuizTest({

    val scheduler = Schedulers.boundedElastic()
    val entitiesCount = REPEATED_KOTEST_AMOUNT

    //==================================================================================================================
    val createQuizRequestsCaption = "create quiz request ($entitiesCount)"
    Given(createQuizRequestsCaption) {
        val logins = createCollection<Login>()
        val templates = Flux.concat(
            createCollection<MonoTemplate>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create template: $it" }
                    TEMPLATE_TEST_AMOUNT++
                    logins.add(generateTestString(10))
                    list.add(createTestQuizTemplate())
                }
            })
            .collectList()
            .publishOn(scheduler)

        When(createQuizRequestsCaption) {
            Then(createQuizRequestsCaption) {
                templates.flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            createTestQuizRequest(it.templateCode, logins)
                        }
                    }
            }
        }

        When(createQuizRequestsCaption) {
            Then(createQuizRequestsCaption) {
                templates.flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            createOrUpdateTestQuizRequest(it.templateCode, logins)
                        }
                    }
            }
        }


        // validate amount of quiz requests
        Then("validate amount of quiz requests ($entitiesCount)") {
            quizRequestCount().awaitSingle().shouldBe(entitiesCount)
        }
    }

    //==================================================================================================================
    val updateStatusCaption = "update status quiz requests  ($entitiesCount)"
    Given(updateStatusCaption) {

        val logins = createCollection<Login>()

        val templates = Flux.concat(
            createCollection<MonoTemplate>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create template: $it" }
                    TEMPLATE_TEST_AMOUNT++
                    logins.add(generateTestString(10))
                    list.add(createTestQuizTemplate())
                }
            })
            .collectList()
            .publishOn(scheduler)

        val codes = createCollection<QuizRequestCode>()

        When(createQuizRequestsCaption) {
            Then(createQuizRequestsCaption) {
                templates.flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            createTestQuizRequest(it.templateCode, logins)
                                .flatMapMany { fromIterable(it) }
                                .flatMap { codes.add(it.requestCode); toMono() }
                        }
                    }
            }
        }

        When(updateStatusCaption) {
            Then(updateStatusCaption) {
                    codes.forEach {
                        runBlocking {
                            updateStatusTestQuizRequest(
                                it,
                                S3_QUIZ_REQUEST_STATUS_CLOSED.entityStatusShortName
                            )
                        }
                    }
            }
        }

    }

    //==================================================================================================================
    val updateStatusStartCaption = "update status start quiz requests  ($entitiesCount)"
    Given(updateStatusStartCaption) {

        val logins = createCollection<Login>()

        val templates = Flux.concat(
            createCollection<MonoTemplate>().also { list ->
                repeat(entitiesCount) {
                    logger().debug { "create template: $it" }
                    TEMPLATE_TEST_AMOUNT++
                    logins.add(generateTestString(10))
                    list.add(createTestQuizTemplate())
                }
            })
            .collectList()
            .publishOn(scheduler)

        val codes = createCollection<QuizRequestCode>()

        When(createQuizRequestsCaption) {
            Then(createQuizRequestsCaption) {
                templates.flatMapMany { fromIterable(it) }
                    .flatMap {
                        runBlocking {
                            createTestQuizRequest(it.templateCode, logins)
                                .flatMapMany { fromIterable(it) }
                                .flatMap { codes.add(it.requestCode); toMono() }
                        }
                    }
            }
        }

        When(updateStatusStartCaption) {
            Then(updateStatusStartCaption) {
                codes.forEach {
                    runBlocking {
                        updateStatusTestStartQuizRequest(
                            it,
                            S3_QUIZ_REQUEST_STATUS_IN_PROGRESS.entityStatusShortName
                        )
                    }
                }
            }
        }

    }
})