package org.dbs.auth.server.consts

import org.dbs.consts.RestHttpConsts.URI_API
import org.dbs.consts.RestHttpConsts.URI_JWT
import org.dbs.consts.RestHttpConsts.URI_LIVENESS_API
import org.dbs.consts.RestHttpConsts.URI_LOGIN
import org.dbs.consts.RestHttpConsts.URI_READINESS_API
import org.dbs.consts.RestHttpConsts.URI_REFRESH
import org.dbs.consts.RestHttpConsts.URI_REFRESH_JWT
import org.dbs.consts.RestHttpConsts.URI_REGISTRY
import org.dbs.consts.SysConst.APP_ACTORS
import org.dbs.consts.SysConst.APP_BANKING_CORE
import org.dbs.consts.SysConst.SOME_APP
import org.dbs.consts.SysConst.SOME_APP_STORE
import org.dbs.consts.SysConst.SOME_APP_V1
import org.dbs.consts.SysConst.SLASH
import org.dbs.customers.consts.ActorsConsts.Routes.URI_ACTOR_CUSTOMER
import org.dbs.customers.consts.ActorsConsts.Routes.URI_ACTOR_MANAGER
import org.dbs.store.consts.SmartSaveSchoolStoreConsts.Routes.URI_MANAGER
import org.dbs.store.consts.SmartSaveSchoolStoreConsts.Routes.URI_VENDOR

object AuthServerConsts {

    const val SB_JWT_STRING_KEY_LENGTH = 128

    object V1 {
        const val ROUTE_USER_V1_SIGN = "/api/auth/sign-in"

        // Query Params
        object Claims {
            const val CL_ACCESS_TOKEN = "ACCESS_TOKEN"
            const val CL_USER_LOGIN = "USER_LOGIN"
            const val CL_USER_PHONE = "USER_PHONE"
            const val CL_USER_FIRST_NAME = "USER_FIRST_NAME"
            const val CL_USER_LAST_NAME = "USER_LAST_NAME"
            const val CL_USER_EMAIL = "USER_EMAIL"
            const val CL_USER_ADDRESS = "USER_ADDRESS"
            const val CL_USER_ID = "USER_ID"
            const val CL_IS_VERIFIED_USER = "IS_VERIFIED_USER"
            const val CL_USER_ROLES = "USER_ROLES"
        }

    }
    // Caches
    object URI {
        private const val URI_V1 = "/v1"
        private const val SAME_ROUTES = URI_API + SLASH + SOME_APP
        const val SOME_URI_LOGIN = SAME_ROUTES + URI_V1 + URI_LOGIN
        const val SOME_URI_LOGIN_V1 = URI_API + URI_V1 + URI_LOGIN
        const val SOME_URI_REFRESH_V1 = URI_API + URI_V1 + URI_JWT + URI_REFRESH
        const val SOME_URI_REGISTRY_V1 = URI_API + URI_V1 + URI_REGISTRY
        const val URI_LIVENESS = SAME_ROUTES + URI_LIVENESS_API
        const val URI_READINESS = SAME_ROUTES + URI_READINESS_API

        // School v1
        private const val USERV1_ROUTES = URI_API + SLASH + SOME_APP_V1
        const val USERV1_LOGIN = USERV1_ROUTES + URI_LOGIN
        const val USERV1_LOGIN_URI = URI_API + URI_LOGIN
        const val USERV1_REFRESH_URI = URI_API + URI_REFRESH_JWT
        const val USERV1_REFRESH = USERV1_ROUTES + URI_REFRESH_JWT
    }

    object YmlKeys {
        const val SOME_ENABLED = "config.restful.security.some.enabled"
        const val SOME_V1_ENABLED = "config.restful.security.some-v1.enabled"
        const val SOME_ENABLED = "config.restful.security.some.enabled"
    }

    object Claims {
        const val CL_JWT_KEY = "JWT_KEY"
        const val CL_TOKEN_KIND = "TOKEN_KIND"
        const val TK_ACCESS_TOKEN = "ACCESS_TOKEN"
        const val TK_REFRESH_TOKEN = "REFRESH_TOKEN"
    }

}
