package org.dbs.banking.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.dbs.banking.core.consts.BankingCoreConsts.Main.PROFILE
import org.dbs.banking.rest.AccountTransactionRest
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_CREATE_INTERNAL_TRANSACTION
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_GET_ACCOUNT_TRANSACTION_CARD
import org.dbs.banking.transactions.consts.BankingTransactionsConsts.Routes.ROUTE_GET_ACCOUNT_TRANSACTION_LIST
import org.dbs.config.AbstractWebSecurityConfig
import org.dbs.consts.SpringCoreConst.Beans.DEFAULT_PROXY_BEANS_VAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.YML_REST_ROUTES_ENABLED
import org.dbs.consts.SysConst.APP_BANKING
import org.dbs.consts.SysConst.APP_BANKING_CAMEL_CASE
import org.dbs.consts.SysConst.SLASH
import org.dbs.consts.SysConst.STRING_TRUE
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration(proxyBeanMethods = DEFAULT_PROXY_BEANS_VAL)
@Primary
@ConditionalOnProperty(name = [PROFILE], havingValue = APP_BANKING)
@OpenAPIDefinition(
    info = Info(
        title = APP_BANKING,
        description = APP_BANKING_CAMEL_CASE
    ),
    servers = [Server(url = SLASH)]
)
class BankingTransactionsRestConfig : AbstractWebSecurityConfig() {
    @Bean
    @SwaggerOpenApiRoutesDefinitions
    @ConditionalOnProperty(name = [YML_REST_ROUTES_ENABLED], havingValue = STRING_TRUE, matchIfMissing = true)
    fun routerRest(
        accountTransactionRest: AccountTransactionRest
    ): RouterFunction<ServerResponse> = addCommonRoutes()
        .andRoute(postRoute(ROUTE_CREATE_INTERNAL_TRANSACTION), accountTransactionRest::createInternalTransaction)
        .andRoute(getRoute(ROUTE_GET_ACCOUNT_TRANSACTION_LIST), accountTransactionRest::getAccountTransactionList)
        .andRoute(getRoute(ROUTE_GET_ACCOUNT_TRANSACTION_CARD), accountTransactionRest::getAccountTransactionCard)
}
