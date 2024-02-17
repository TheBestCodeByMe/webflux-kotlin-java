package org.dbs.auth.server.service

import org.dbs.spring.core.api.AbstractApplicationService
import org.dbs.application.core.service.funcs.ServiceFuncs.createMap
import org.dbs.auth.server.model.Application
import org.dbs.auth.server.repo.ApplicationRepo
import org.dbs.consts.SysConst.UNCHECKED_CAST
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.util.stream.Collectors.toList

@EnableConfigurationProperties
@Lazy(false)
@Service
class RefsService(applicationRepo: ApplicationRepo) : AbstractApplicationService() {
    private val applications: Map<Int, String> = createMap()
    private val applicationRepo: ApplicationRepo

    init {
        this.applicationRepo = applicationRepo
    }

    @Suppress(UNCHECKED_CAST)
    open fun synchronizeRefs() {
        logger.info("synchronize system references")

        @Suppress("UNCHECKED_CAST")
        applicationRepo.saveAll(
            applications
                .entries
                .stream()
                .map { (key: Int, value: String): Map.Entry<Int, String> ->
                    {
                        val recs = value.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val code = recs[0]
                        val name = recs[1]

                        Application(
                            applicationId = key,
                            applicationCode = code,
                            applicationName = name
                        )
                    }
                }.collect(toList()) as MutableList<Application>
        ).subscribe()
    }
}
