package org.dbs.quiz.rest

import org.dbs.quiz.service.QuizRequestService
import org.dbs.quiz.value.CreateOrUpdateQuizRequestValueRequest
import org.dbs.quiz.value.quiz.request.UpdateStatusQuizRequestValueRequest
import org.dbs.quiz.value.quiz.request.UpdateStatusStartQuizRequestValueRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest as R

@Service
class QuizRequestRest(private val quizRequestService: QuizRequestService) : H1_PROCESSOR<R>() {
    suspend fun createOrUpdateQuizRequest(serverRequest: R) = serverRequest.doRequestCo {
        CreateOrUpdateQuizRequestValueRequest(it).buildResponse(this, quizRequestService)
    }

    suspend fun updateStatusQuizRequest(serverRequest: R) = serverRequest.doRequestCo {
        UpdateStatusQuizRequestValueRequest(it).buildResponse(this, quizRequestService)
    }

    suspend fun updateStatusStartQuizRequest(serverRequest: R) = serverRequest.doRequestCo {
        UpdateStatusStartQuizRequestValueRequest(it).buildResponse(this, quizRequestService)
    }
}
