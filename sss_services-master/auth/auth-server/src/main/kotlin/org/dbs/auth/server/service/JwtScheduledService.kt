package org.dbs.auth.server.service

import org.dbs.ext.CoroutineFuncs.isReadyToReceive
import org.dbs.spring.core.api.ScheduledAbstractApplicationService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.dbs.auth.server.api.ApplicationLogin4RevokeDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime.now
import java.util.concurrent.TimeUnit.SECONDS

@Lazy(false)
@Service
class JwtScheduledService(
    private val jwtStorageService: JwtStorageService,
) : ScheduledAbstractApplicationService() {

    @Value("\${config.security.jwt.delete-deprecated.expiryDays:60}")
    val expiryDays = 60L // days

    @Value("\${config.security.jwt.delete-deprecated.period:3600}")
    val refreshPeriod = 3600L // seconds

    // Manage deprecated JWT
    @Scheduled(
        initialDelayString = "5",
        fixedRateString = "\${config.security.jwt.delete-deprecated.period:60}",
        timeUnit = SECONDS
    )
    fun scheduledProcedure() = runBlocking { processDeprecatedJwt() }

    suspend fun processDeprecatedJwt() = coroutineScope {
        launch {

            val now = now()
            val nextRun = "next run: ${now.plusSeconds(refreshPeriod)}"

            jwtStorageService.arcDeprecatedJwt(now.also { $nextRun } })
                .then(
                    jwtStorageService.deleteDeprecatedJwt(
                        now.minusDays(expiryDays)
                            .also { logger.debug { "delete deprecated jwt (issued before $it, $expiryDays day(s); $nextRun" } })
                )
                .subscribe()
        }
    }

    override fun initialize() = super.initialize()
}
