package org.dbs.auth.server.clients.v1.service

import org.dbs.application.core.api.LateInitVal
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.application.core.service.funcs.StringFuncs.ipOnly
import org.dbs.application.core.service.funcs.StringFuncs.last15
import org.dbs.auth.server.JwtParamsDto
import org.dbs.auth.server.clients.v1.client.V1Client
import org.dbs.auth.server.consts.AuthServerConsts.Claims.CL_TOKEN_KIND
import org.dbs.auth.server.consts.AuthServerConsts.Claims.TK_ACCESS_TOKEN
import org.dbs.auth.server.consts.AuthServerConsts.Claims.TK_REFRESH_TOKEN
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_ROLES
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_ADDRESS
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_EMAIL
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_FIRST_NAME
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_ID
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_LAST_NAME
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_LOGIN
import org.dbs.auth.server.consts.AuthServerConsts.V1.Claims.CL_USER_PHONE
import org.dbs.auth.server.consts.AuthServerConsts.YmlKeys.SOME_V1_ENABLED
import org.dbs.auth.server.enums.ApplicationEnum.S3_USER
import org.dbs.auth.server.model.IssuedJwt
import org.dbs.auth.server.model.RefreshJwt
import org.dbs.auth.server.service.JwtStorageService
import org.dbs.component.JwtSecurityService
import org.dbs.consts.SecurityConsts.Claims.CL_IP
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_V1_ACCESS_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_V1_REFRESH_EXPIRATION_TIME
import org.dbs.consts.SpringCoreConst.PropertiesNames.JWT_V1_SECRET_KEY
import org.dbs.consts.SysConst.EMPTY_STRING
import org.dbs.consts.SysConst.LONG_ZERO
import org.dbs.consts.SysConst.STRING_TRUE
import org.dbs.rest.service.value.AbstractRestApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import javax.crypto.SecretKey

@Service
@ConditionalOnProperty(name = [SOME_V1_ENABLED], havingValue = STRING_TRUE)
class SomeV1SecurityService(
    private val v1Client: V1Client,
    private val jwtSecurityService: JwtSecurityService,
    val jwtStorageService: JwtStorageService,
) : AbstractRestApplicationService() {

    @Value("\${$JWT_V1_SECRET_KEY:ThisIsSecretForJWTHS512SignatureAlgorithmThat}")
    private val secretKey = EMPTY_STRING

    @Value("\${$JWT_V1_ACCESS_EXPIRATION_TIME:28801}")
    private val expirationTime = LONG_ZERO

    @Value("\${$JWT_V1_REFRESH_EXPIRATION_TIME:57601}")
    private val refreshExpirationTime = LONG_ZERO

    private val key by lazy { LateInitVal<SecretKey>() }
    override fun initialize() = super.initialize().also {
        key.hold(jwtSecurityService.buildKey(secretKey))
    }

    fun createRefreshJwt(jwtId: Long, login: String, ip: String): Mono<RefreshJwt> =
        createRefreshJwt(jwtId, login, refreshExpirationTime, ip)


    private fun createRefreshJwt(jwtId: Long, login: String, expirationTime: Long, ip: String)
            : Mono<RefreshJwt> = StringBuilder(512).run {
        val validUntil = now().plusSeconds(expirationTime)
        append(jwtId.toString().plus(";"))
        append(login.plus(";"))
        append(ip.plus(";"))
        createRefreshTokenInternal(jwtId, this.toString(), validUntil, CL_USER_LOGIN, login, ip)
    }

    fun createAccessJwt(jwtParams: JwtParamsDto): Mono<IssuedJwt> = createJwtInternal(jwtParams)

    private fun createJwtInternal(jwtParams: JwtParamsDto) = jwtParams.run {

        require(lastName.isNotEmpty()) { "lastName should be not empty" }
        require(userId > 0) { "userId should be not empty" }
        require(roles.isNotEmpty()) { "roles should be not empty" }
        require(userLogin.isNotEmpty()) { "userLogin should be not empty" }
        require(firstName.isNotEmpty()) { "firstName should be not empty" }

        val tokenKey = StringBuilder(512).also { sb ->
            sb.append(userLogin.plus(";"))
            sb.append(firstName.plus(";"))
            sb.append(lastName.plus(";"))
            sb.append(userId.toString().plus(";"))

            phone?.let { sb.append(it.plus(";")) }
            email?.let { sb.append(it.plus(";")) }
            address?.let { sb.append(it.plus(";")) }
        }.toString()

        jwtStorageService.createAndSaveAccessJwt(
            tokenKey,
            now().plusSeconds(expirationTime),
            S3_USER,
            userLogin
        ) {

            jwtSecurityService.generateJwt(
                this.javaClass.packageName,
                createMap { map ->
                    map[CL_USER_LOGIN] = userLogin
                    map[CL_USER_FIRST_NAME] = firstName
                    map[CL_USER_LAST_NAME] = lastName
                    map[CL_ROLES] = roles
                    map[CL_USER_ID] = userId.toString()
                    map[CL_TOKEN_KIND] = TK_ACCESS_TOKEN
                    phone?.let { map[CL_USER_PHONE] = it }
                    email?.let { map[CL_USER_EMAIL] = it }
                    address?.let { map[CL_USER_ADDRESS] = it }
                },
                expirationTime,
                key.value
            )
        }
    }

    private fun createRefreshTokenInternal(
        parentJwtId: Long,
        tokenKey: String,
        validUntil: LocalDateTime,
        claimKey: String,
        claimKeyValue: String,
        ip: String,
    ): Mono<RefreshJwt> =
        jwtStorageService.createAndSaveRefreshJwt(parentJwtId, validUntil, tokenKey) {
            val claims = createMap {
                it[CL_IP] = ip.ipOnly()
                it[claimKey] = claimKeyValue
                it[CL_TOKEN_KIND] = TK_REFRESH_TOKEN
            }
            jwtSecurityService.generateJwt(this.javaClass.packageName, claims, refreshExpirationTime, key.value)
        }
}
