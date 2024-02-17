package org.dbs.auth.server.service

import org.dbs.application.core.service.funcs.ServiceFuncs.createCollection
import org.dbs.auth.server.consts.AuthConsts.Consumers.HTTP_PRODUCER_ID
import org.dbs.auth.server.dao.HttpServerRequestsDao
import org.dbs.auth.server.model.HttpServerRequest
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_PROCESSING_CRON
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_PROCESSING_INTERVAL
import org.dbs.consts.SpringCoreConst.PropertiesNames.KAFKA_PROCESSING_INTERVAL_DEF
import org.dbs.kafka.consts.KafkaConsts.Topics.HTTP_REGISTRY_TOPIC
import org.dbs.rest.kafka.HttpRequestMessage
import org.dbs.spring.core.api.AbstractApplicationService
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Lazy(false)
@Service
class HttpRequestsService(
    httpServerRequestsDao: HttpServerRequestsDao,
    refsService: RefsService
) : AbstractApplicationService() {
    private val receivedRequests: MutableCollection<HttpRequestMessage> = createCollection()
    private val httpServerRequestsDao: HttpServerRequestsDao
    private val refsService: RefsService

    init {
        this.httpServerRequestsDao = httpServerRequestsDao
        this.refsService = refsService
    }

    @KafkaListener(id = HTTP_REGISTRY_TOPIC, groupId = HTTP_PRODUCER_ID, topics = [HTTP_REGISTRY_TOPIC])
    fun receiveRequests(requests: Collection<HttpRequestMessage>) {
        receivedRequests.addAll(requests)
    }

    @Scheduled(
        fixedRateString = "\${$KAFKA_PROCESSING_INTERVAL:$KAFKA_PROCESSING_INTERVAL_DEF}",
        cron = "\${$KAFKA_PROCESSING_CRON:}"
    )
    fun insertFromKafka() {
        if (!receivedRequests.isEmpty()) {
            synchronized(this) {
                val removeList: MutableCollection<HttpRequestMessage> = createCollection()
                httpServerRequestsDao.saveRequests(
                    receivedRequests
                        .stream()
                        .map { rm: HttpRequestMessage ->
                            val httpServerRequest = HttpServerRequest()
                            removeList.add(rm)
                            httpServerRequest.asNew<HttpServerRequest>()
                        }.collect(Collectors.toList())
                ).subscribe { e: HttpServerRequest -> logger.debug("request: ${e.id}") }
                receivedRequests.removeAll(removeList.toSet())
            }
        }
    }

    override fun initialize() {
        super.initialize()
    }
}
