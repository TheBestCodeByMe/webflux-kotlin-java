package org.dbs.quiz.rest

import org.dbs.quiz.service.TemplateService
import org.dbs.quiz.value.CreateOrUpdateTemplateValueRequest
import org.dbs.quiz.value.GetQuizTemplateCardRequest
import org.dbs.quiz.value.GetQuizTemplateHistListRequest
import org.dbs.quiz.value.GetQuizTemplateListRequest
import org.dbs.quiz.value.UpdateStatusTemplateValueRequest
import org.dbs.rest.api.consts.H1_PROCESSOR
import org.dbs.rest.service.ServerRequestFuncs.doRequestCo
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest as R

@Service
class TemplateRest(private val templateService: TemplateService) : H1_PROCESSOR<R>() {
    suspend fun createOrUpdateTemplate(serverRequest: R) = serverRequest.doRequestCo {
        CreateOrUpdateTemplateValueRequest(it).buildResponse(this, templateService)
    }

    suspend fun updateStatusTemplate(serverRequest: R) = serverRequest.doRequestCo {
        UpdateStatusTemplateValueRequest(it).buildResponse(this, templateService)
    }

    suspend fun getTemplateCard(serverRequest: R) = serverRequest.doRequestCo {
        GetQuizTemplateCardRequest(it).buildResponse(this)
    }

    suspend fun getTemplateList(serverRequest: R) = serverRequest.doRequestCo {
        GetQuizTemplateListRequest(it).buildResponse(this)
    }

    suspend fun getTemplateHistList(serverRequest: R) = serverRequest.doRequestCo {
        GetQuizTemplateHistListRequest(it).buildResponse(this)
    }

}
