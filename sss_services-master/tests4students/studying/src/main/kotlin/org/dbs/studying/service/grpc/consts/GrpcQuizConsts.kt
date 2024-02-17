package org.dbs.quiz.service.grpc.consts

import org.dbs.consts.CKI
import org.dbs.consts.CKS
import org.dbs.consts.CTX

object GrpcQuizConsts {

    object ContextKeys {
        val CK_MANAGER_LOGIN: CKS = CTX.key("MANAGER_LOGIN")
        val CK_MANAGER_ID: CKI = CTX.key("MANAGER_ID")

        val CK_CREATE_OR_UPDATE_TEMPLATE_PROCEDURE: CKS = CTX.key("createOrUpdateTemplate")
        //val CK_MANAGER_UPDATE_PASSWORD_PROCEDURE: CKS = CTX.key("updateManagerPassword")

    }
}
