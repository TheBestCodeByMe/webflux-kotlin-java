package org.dbs.quiz.service.grpc.actors.client

import com.google.protobuf.Any
import org.dbs.actor.service.GrpcActorsClientService
import org.dbs.application.core.api.CollectionLateInitVal
import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.consts.Login
import org.dbs.consts.SpringCoreConst
import org.dbs.consts.SpringCoreConst.PropertiesNames.KOTEST_MODE
import org.dbs.customers.consts.CustomerLogin
import org.dbs.grpc.ext.QueryParamsList.createQueryParam
import org.dbs.grpc.ext.QueryParamsList.createQueryParamsList
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginDto
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginList
import org.dbs.protobuf.actors.GetSchoolCustomerByLoginResponse
import org.dbs.protobuf.core.QueryParamEnum.QPG_SCHOOL_CUSTOMER_LOGINS
import org.dbs.protobuf.core.ResponseAnswer
import org.dbs.protobuf.quiz.CreateQuizRequestResponse
import org.dbs.service.EntityResponseBuilder
import org.dbs.service.GrpcResponse
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ActorsClientService(private val actorsClientService: GrpcActorsClientService) : AbstractApplicationService() {

    @Value("\${$KOTEST_MODE:false}")
    private val kotestMode = false

    suspend fun getSchoolCustomerByLogins(
        customerLogins: CustomerLogin,
    ): GetSchoolCustomerByLoginResponse =
        if (!kotestMode) {
            actorsClientService.getCustomerByLogins(
                createQueryParamsList(
                    createQueryParam(QPG_SCHOOL_CUSTOMER_LOGINS, customerLogins),
                )
            )
        } else {
            val logins by lazy { CollectionLateInitVal<Login>() }
            logins.hold(customerLogins.split(","))
            val customers = createCollection<GetSchoolCustomerByLoginDto>()
            var count = 0L
            logins.value.forEach { login ->
                customers.add(
                    GetSchoolCustomerByLoginDto.newBuilder()
                    .setCustomerId(count++)
                    .setLogin(login)
                    .build())
            }
            val scCustList = GetSchoolCustomerByLoginList.newBuilder().addAllCustomers(customers).build()

            val grpcResponse: GrpcResponse<GetSchoolCustomerByLoginResponse> by lazy {
                {
                    GetSchoolCustomerByLoginResponse.newBuilder().setResponseAnswer(it).build()
                }
            }

            grpcResponse(ResponseAnswer.newBuilder().setResponseEntity(Any.pack(scCustList)).build())
        }
}
