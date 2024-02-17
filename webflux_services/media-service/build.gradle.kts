import dsl.Dependencies.ApplicationAttributes.MAIN_CLASS
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_TITLE
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_VERSION
import dsl.Dependencies.ProjectAttributes.MAIN_CLASS_NAME
import dsl.Dependencies.ProjectAttributes.NO_VERSION
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.MEDIA_STARTER
import dsl.Dependencies.Projects.MEDIA_API
import dsl.Dependencies.Projects.SECURITY_CONFIG_STARTER
import dsl.Dependencies.Projects.SPRING_BOOT_API
import dsl.Dependencies.Projects.SPRING_CONFIG
import dsl.Dependencies.Projects.SPRING_CORE_API
import dsl.Dependencies.Projects.TEST_CORE

plugins {
    application
    idea
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
//    id("com.github.johnrengelman.shadow")
}

val springBootGroup = "org.springframework.boot"
val springDocGroup = "org.springdoc"
val googleGroup = "com.google.code.gson"
// exludes
val springExcStarter = "spring-boot-starter"
val springExcStarterLogging = "spring-boot-starter-logging"
val mainApplicationClassName = "org.dbs.media.SmartSafeSchoolMediaApplication"

dependencies {

    api(project(APPLICATION_CORE_API))
    api(project(MEDIA_STARTER))
    api(project(MEDIA_API))
    api(project(SECURITY_CONFIG_STARTER))
    api(project(SPRING_BOOT_API))
    api(project(SPRING_CONFIG))
    api(project(SPRING_CORE_API))

    testApi(project(TEST_CORE))
    testApi(libs.grpc.testing)
    testApi(libs.spring.starter.test)
    testApi(libs.testcontainers.junit.jupiter)

    implementation(libs.commons.lang3)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.spring.actuator)
    implementation(libs.spring.configuration.processor)
    implementation(libs.spring.json)
    implementation(libs.spring.security)
    implementation(libs.spring.starter)
    implementation(libs.jboss.logging)
    implementation(libs.log4j.api.kotlin)
    implementation(libs.reactor.kotlin.extensions)
    implementation(libs.spring.webflux)
    implementation(libs.springdoc.openapi.webflux.ui)
    implementation(libs.swagger.annotations)


    compileOnly(libs.kotlin.reflect)
    runtimeOnly(libs.coroutines.core.jvm)

    annotationProcessor(libs.spring.configuration.processor)
    implementation(libs.kotlin.stdlib.jdk8)

    configurations {
        all {
            exclude(group = springBootGroup, module = "spring-boot-starter-logging")
            exclude(group = springBootGroup, module = "junit-vintage-engine")
            exclude(group = springDocGroup, module = "jackson-core")
            //exclude(group = googleGroup, module = "gson")
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
    project.version = NO_VERSION
    project.setProperty(MAIN_CLASS_NAME, mainApplicationClassName)
    archiveBaseName.set(project.name)
}

description = "media-service"

tasks.test {
    useJUnitPlatform()
}
