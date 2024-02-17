package org.dbs.auth.server.clients.v1.service

import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.auth.server.consts.AuthServerConsts.URI.SOME_USERV1_LOGIN_URI
import org.dbs.auth.server.consts.AuthServerConsts.URI.SOME_USERV1_REFRESH_URI
import org.dbs.auth.server.consts.AuthServerConsts.YmlKeys.SOME_V1_ENABLED
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SysConst.APP_AUTH_SOME_CAMEL_CASE
import org.dbs.consts.SysConst.SOME_APP_V1
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_TRUE
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@ConditionalOnProperty(name = [SOME_V1_ENABLED], havingValue = STRING_TRUE)
@OpenAPIDefinition(
    info = Info(
        title = APP_AUTH_SOME_CAMEL_CASE,
        description = APP_AUTH_SOME_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
class SomeV1RestConfig(val someV1SecurityRest: SmartSafeSchoolV1SecurityRest):
    AbstractWebSecurityConfig(SOME_APP_V1) {

    override fun addSecurityRoutes(): RouterFunction<ServerResponse> =
        route(postRoute(SOME_USERV1_LOGIN_URI), someV1SecurityRest::doUserV1Login)
            .andRoute(
                postRoute(SOME_USERV1_REFRESH_URI),
                someV1SecurityRest::doUserV1RefreshJwt
            )

    @OpenApiV1RoutesDefinitions
    @Bean
    fun routerSmartSafeSchoolV1AuthorizationRest(): RouterFunction<ServerResponse> = addCommonRoutes()
    
}
