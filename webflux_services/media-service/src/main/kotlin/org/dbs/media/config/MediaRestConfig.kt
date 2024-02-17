package org.dbs.media.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_REST_ROUTES_ENABLED
import org.dbs.consts.SysConst.SOME_APP_MEDIA_CAMEL_CASE
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.media.rest.MediaRest
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.Routes.ROUTE_DOWNLOAD_MEDIA_FILE
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.Routes.ROUTE_UPLOAD_MEDIA_FILE
import org.dbs.store.consts.SmartSaveSchoolMediaConsts.Routes.ROUTE_UPLOAD_MEDIA_FILE_V2
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@Lazy(false)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@Primary
@OpenAPIDefinition(
    info = Info(
        title = SOME_APP_MEDIA_CAMEL_CASE,
        description = SOME_APP_MEDIA_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
class MediaRestConfig : AbstractWebSecurityConfig() {

    @Bean
    @OpenApiRoutesDefinitions
    @ConditionalOnProperty(name = [YML_REST_ROUTES_ENABLED], havingValue = STRING_TRUE, matchIfMissing = true)
    fun routerSmartSafeSchoolRest(
        mediaRest: MediaRest
    ): RouterFunction<ServerResponse> = addCommonRoutes()
        .andRoute(postRoute(ROUTE_UPLOAD_MEDIA_FILE), mediaRest::uploadFile)
        .andRoute(getRoute(ROUTE_DOWNLOAD_MEDIA_FILE), mediaRest::downloadFile)
}
