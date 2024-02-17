package org.dbs.quiz.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_REST_ROUTES_ENABLED
import org.dbs.consts.SysConst.APP_QUIZ
import org.dbs.consts.SysConst.APP_QUIZ_CAMEL_CASE
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.quiz.consts.QuizConsts.Main.PROFILE
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_CREATE_OR_UPDATE_QUIZ_REQUEST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_CREATE_OR_UPDATE_TEMPLATE
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_GET_TEMPLATES_LIST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_GET_TEMPLATE_CARD
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_GET_TEMPLATE_HIST_LIST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_UPDATE_STATUS_QUIZ_REQUEST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_UPDATE_STATUS_START_QUIZ_REQUEST
import org.dbs.quiz.consts.QuizConsts.Routes.ROUTE_UPDATE_STATUS_TEMPLATE
import org.dbs.quiz.rest.QuizRequestRest
import org.dbs.quiz.rest.TemplateRest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@Primary
@ConditionalOnProperty(name = [PROFILE], havingValue = APP_QUIZ)
@OpenAPIDefinition(
    info = Info(
        title = APP_QUIZ,
        description = APP_QUIZ_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
class StudyingRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    @ConditionalOnProperty(name = [YML_REST_ROUTES_ENABLED], havingValue = STRING_TRUE, matchIfMissing = true)
    fun routerQuizRest(
        templateRest: TemplateRest,
        quizRequestRest: QuizRequestRest
    ): RouterFunction<ServerResponse> = coRouter {
        // template
        addPostRoute(ROUTE_CREATE_OR_UPDATE_TEMPLATE, templateRest::createOrUpdateTemplate)
        addPostRoute(ROUTE_UPDATE_STATUS_TEMPLATE, templateRest::updateStatusTemplate)
        addGetRoute(ROUTE_GET_TEMPLATE_CARD, templateRest::getTemplateCard)
        // template list
        addGetRoute(ROUTE_GET_TEMPLATES_LIST, templateRest::getTemplateList)
        addGetRoute(ROUTE_GET_TEMPLATE_HIST_LIST, templateRest::getTemplateHistList)
        // quiz request
        addPostRoute(ROUTE_CREATE_OR_UPDATE_QUIZ_REQUEST, quizRequestRest::createOrUpdateQuizRequest)
        addPostRoute(ROUTE_UPDATE_STATUS_QUIZ_REQUEST, quizRequestRest::updateStatusQuizRequest)
        addPostRoute(ROUTE_UPDATE_STATUS_START_QUIZ_REQUEST, quizRequestRest::updateStatusStartQuizRequest)
//        addCommonRoutesCo()
//            // Templates
//            .andRoute(postRoute(ROUTE_CREATE_OR_UPDATE_TEMPLATE), templateRest::createOrUpdateTemplate)
//            .andRoute(getRoute(ROUTE_GET_MANAGER_CREDENTIALS), managerRest::getManagerCredentials)
    }
}
