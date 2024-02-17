
import dsl.Dependencies.ApplicationAttributes.MAIN_CLASS
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_TITLE
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_VERSION
import dsl.Dependencies.ProjectAttributes.MAIN_CLASS_NAME
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.BANKING_TRANSACTION_API
import dsl.Dependencies.Projects.CACHE_STARTER
import dsl.Dependencies.Projects.ENTITY_CORE_API
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.NO_VERSION_ASSIGNED
import dsl.Dependencies.Projects.PMT_VISA_CLIENT
import dsl.Dependencies.Projects.PMT_VISA_PROTO_API
import dsl.Dependencies.Projects.PROTOBUF_API_SRC
import dsl.Dependencies.Projects.R2DBC_STARTER
import dsl.Dependencies.Projects.REF_STARTER
import dsl.Dependencies.Projects.SECURITY_CONFIG_STARTER
import dsl.Dependencies.Projects.SECURITY_MANAGER_API
import dsl.Dependencies.Projects.SPRING_BOOT_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.SPRING_KAFKA_STARTER
import dsl.Dependencies.Projects.SPRING_REST_API
import dsl.Dependencies.Projects.STD_LIB
import dsl.Dependencies.Projects.TEST_CORE

plugins {
    application
    idea
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.gitlab.arturbosch.detekt")
}


val springBootGroup = "org.springframework.boot"
val springDocGroup = "org.springdoc"
val googleGroup = "com.google.code.gson"
val springExcStarter = "spring-boot-starter"
val springExcStarterLogging = "spring-boot-starter-logging"
val mainApplicationClassName = "org.dbs.banking.BankingApplication"

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(BANKING_TRANSACTION_API))
    api(project(CACHE_STARTER))
    api(project(ENTITY_CORE_API))
    api(project(KAFKA_API))
    api(project(PROTOBUF_API_SRC))
    api(project(R2DBC_STARTER))
    api(project(REF_STARTER))
    api(project(SECURITY_CONFIG_STARTER))
    api(project(SPRING_BOOT_API))
    api(project(SPRING_CONFIG))
    api(project(SPRING_CORE_API))
    api(project(SPRING_KAFKA_STARTER))
    api(project(SPRING_REST_API))
    testApi(project(TEST_CORE))

    implementation(kotlin(STD_LIB))
    implementation(libs.spring.actuator)
    implementation(libs.spring.security)
    implementation(libs.spring.autoconfigure)
    implementation(libs.spring.configuration.processor)
    implementation(libs.spring.data.r2dbc)
    implementation(libs.spring.data.redis)
    implementation(libs.r2dbc.pool)
    implementation(libs.jjwt.api)
    implementation(libs.jboss.logging)
    implementation(libs.jboss.marshalling)
    implementation(libs.commons.lang3)
    implementation(project(PMT_VISA_PROTO_API))
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.log4j.bom))
    implementation(libs.reactor.netty)
    implementation(libs.spring.json)
    implementation(libs.spring.starter)
    implementation(libs.spring.kafka)
    implementation(libs.spring.webflux)
    implementation(libs.swagger.annotations)
    implementation(libs.springdoc.openapi.webflux.ui)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.spring.data.redis)
    implementation(libs.spring.data.redis.reactive)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.core.jvm)
    implementation(libs.coroutines.reactor)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.script.runtime)
    implementation(libs.commons.collections4)

    implementation(libs.log4j.api.kotlin)
    implementation(libs.log4j.jcl)
    implementation(libs.bucket4j.core)
    implementation(libs.grpc.all)

    testApi(libs.postgresql)
    testApi(libs.coroutines.test)
    testApi(libs.lincheck)
    testApi(libs.kotest.assertions.core)
    testApi(libs.kotest.runner.junit5.jvm)
    testApi(platform(libs.kotest.bom))
    testApi(libs.reactor.test)
    testApi(libs.spring.starter.test)
    testApi(libs.testcontainers.junit.jupiter)
    testApi(libs.testcontainers.postgresql)
    testApi(libs.kotest.property.jvm)
    testApi(libs.kotest.extensions.spring)
    testApi(libs.kotest.testcontainers)

    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.kotlin.noarg)
    compileOnly(libs.spring.autoconfigure.processor)
    compileOnly(libs.kotlin.osgi.bundle)

    annotationProcessor(libs.spring.configuration.processor)
    annotationProcessor(libs.spring.autoconfigure.processor)

    configurations {
        all {
            exclude(group = springBootGroup, module = "spring-boot-starter-logging")
            exclude(group = springBootGroup, module = "junit-vintage-engine")
            exclude(group = springDocGroup, module = "jackson-core")
        }
    }
}

springBoot {
    buildInfo()
}

tasks.jar {
    manifest {
        attributes[MAIN_CLASS] = mainApplicationClassName
        attributes[IMPLEMENTATION_TITLE] = project.name
        attributes[IMPLEMENTATION_VERSION] = project.version
    }
    project.version = NO_VERSION_ASSIGNED
    project.setProperty(MAIN_CLASS_NAME, mainApplicationClassName)
    archiveBaseName.set(project.name)
}

application {
    mainClass.set(mainApplicationClassName)
}

description = "banking"

tasks.test {
    useJUnitPlatform()
}
