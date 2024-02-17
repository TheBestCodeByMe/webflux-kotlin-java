
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import dsl.Dependencies.ApplicationAttributes.MAIN_CLASS
import dsl.Dependencies.Core.JVM_VERSION
import dsl.Dependencies.Core.KOTLIN_LANG_VERSION
import dsl.Dependencies.Core.PRODUCT_DESCRIPTION
import dsl.Dependencies.Core.PRODUCT_VERSION
import dsl.Dependencies.Core.PROJECT_GROUP
import dsl.Dependencies.Core.ROOT_PROJECT
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_TITLE
import dsl.Dependencies.ProjectAttributes.IMPLEMENTATION_VERSION
import dsl.Dependencies.ProjectAttributes.MAIN_CLASS_NAME
import dsl.Dependencies.ProjectAttributes.NO_VERSION
import dsl.Dependencies.Spring.apacheLoggingGroup
import dsl.Dependencies.Spring.jsonWebTokenGroup
import dsl.Dependencies.Spring.projectReactorGroup
import dsl.Dependencies.Spring.springBootCloudGroup
import dsl.Dependencies.Spring.springBootGroup
import dsl.Dependencies.Spring.springBootKafkaGroup
import dsl.Dependencies.Spring.springDevh
import dsl.Dependencies.Spring.springDocGroup
import dsl.Dependencies.Spring.springExcStarterLogging
import dsl.Dependencies.Spring.springFasterXmlGroup
import dsl.Dependencies.Spring.springGrpc
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension.Companion.DEFAULT_SRC_DIR_KOTLIN
import io.gitlab.arturbosch.detekt.extensions.DetektExtension.Companion.DEFAULT_TEST_SRC_DIR_KOTLIN
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.file.DuplicatesStrategy.EXCLUDE
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0

plugins {
    application
    // jvm
    base
    `java-library`
    `jvm-test-suite`
    `java-gradle-plugin`
    `version-catalog`
    id("org.dbs.java-conventions")
    // spring
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.spring.javadoc-aggregate")
    id("io.reflectoring.spring-boot-devtools")
    // kotlin
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.scripting")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("org.jetbrains.kotlin.plugin.noarg")
    // arturbosch
    id("io.gitlab.arturbosch.detekt")
    // spotless
    id("com.diffplug.spotless")
    //alias(libs.plugins.gradleVersions)
    // jacoco
    jacoco
    //id ("org.kordamp.gradle.project'
    // jetbrains
    idea
    checkstyle
    signing
    // gradle
    id("org.gradle.test-retry")
    id("org.jetbrains.gradle.plugin.idea-ext")
    //id ("com.jvadev.gradle-kotlin-common-plugin' version '2.1.0'
    //  gradle
    id("org.gradle.wrapper-upgrade")
    id("org.gradle.crypto.checksum")
    // swagger
    id("io.swagger.core.v3.swagger-gradle-plugin")
    // other
    id("org.kordamp.gradle.project-enforcer")
    id("com.github.slamdev.openapi-spring-generator")
    id("com.autonomousapps.plugin-best-practices-plugin")
    // grpc
    id("com.google.protobuf")
    // other
    id("com.github.ben-manes.versions")
    // graalVM
    //id("org.graalvm.buildtools.native")
}

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    output.set(layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

allprojects {
    apply(plugin = "base")
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "jvm-test-suite")
    apply(plugin = "java-gradle-plugin")
    apply(plugin = "org.dbs.java-conventions")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.jetbrains.kotlin.plugin.scripting")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.spring.javadoc-aggregate")
    apply(plugin = "idea")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")
    apply(plugin = "jacoco")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.diffplug.spotless")
    //
    apply(plugin = "org.gradle.wrapper-upgrade")
    //apply(plugin = "com.jvadev.gradle-kotlin-common-plugin'
    apply(plugin = "checkstyle")
    //apply(plugin = "dev.gradleplugins.java-gradle-plugin'
    apply(plugin = "org.kordamp.gradle.project-enforcer")
    apply(plugin = "io.swagger.core.v3.swagger-gradle-plugin")
    apply(plugin = "com.autonomousapps.plugin-best-practices-plugin")
    apply(plugin = "io.reflectoring.spring-boot-devtools")
    //apply(plugin = "license'
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "org.gradle.test-retry")
    // Detekt
    detekt {
        allRules = true
        source.setFrom(
            DEFAULT_SRC_DIR_KOTLIN,
            DEFAULT_TEST_SRC_DIR_KOTLIN
        )
        config.setFrom(file("$rootDir/buildSrc/src/main/resources/detekt.yml"))
        basePath = "$rootDir"
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    // spotless
    spotless {
//        kotlin {
//            // by default the target is every '.kt' and '.kts` file in the java sourcesets
//            ktfmt()    // has its own section below
//            ktlint()   // has its own section below
//            diktat()   // has its own section below
//            prettier() // has its own section below
//            //licenseHeader '/* (C)$YEAR */' // or licenseHeaderFile
//        }
//        kotlinGradle {
//            //target '*.gradle.kts' // default target for kotlinGradle
//            ktlint() // or ktfmt() or prettier()
//        }
    }

    dependencies {

    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = JVM_VERSION
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
            md.required.set(true)
        }
        basePath = rootDir.absolutePath
        finalizedBy(detektReportMergeSarif)
    }

    detektReportMergeSarif {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = JVM_VERSION
    }

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = JVM_VERSION
    }

    // tasks
    tasks.named("dependencyUpdates").configure {
        // configure the task, for example wrt. resolution strategies

    }

    tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {

        // optional parameters
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        google()
    }

//    tasks.withType<AbstractCopyTask>().configureEach {
//        //exclude("**/*.sql")
//        exclude("**/sql/**")
//    }

}

dependencies {

    implementation(kotlin("stdlib"))
    // spring boot dependencies
    implementation(platform(libs.kotlin.bom))
    implementation(platform(libs.spring.dependencies))
    //implementation enforcedPlatform(libs.spring.dependencies)

    // spring
    implementation(libs.spring.boot) { exclude(springExcStarterLogging) }
    implementation(libs.spring.starter) { exclude(springExcStarterLogging) }

    // spring cloud
    implementation(libs.spring.cloud.loadbalancer)
    implementation(libs.spring.cloud.starter.gateway) {
        exclude(springBootGroup, springExcStarterLogging)
    }
    implementation(libs.spring.cloud.dependencies)
    implementation(libs.spring.cloud.netflix.eureka.client)

    // yaml
    implementation(libs.snakeyaml)

    // spring security
    implementation(libs.spring.security.jwt)

    //r2dbc
    implementation(libs.r2dbc.pool)
    //implementation(libs.r2dbc.postgres)

    // reactorTools
    implementation(libs.reactor.tools)
    implementation(libs.reactor.netty)
    implementation(libs.reactor.kotlin.extensions)
//    testApi(libs.blockhound.junit.platform

    // reflections
    implementation(libs.reflection)

    // kafka
    implementation(libs.spring.kafka)

    // redis
    implementation(libs.jedis)

    //jwt
    implementation(libs.jwt.auth)
    implementation(libs.nimbus.jose.jwt)
    implementation(libs.jjwt.api)

    //jmh
    implementation(libs.jmh.core)
    implementation(libs.jmh.annotations)

    // Mongo
    implementation(libs.spring.mongodb)
    implementation(libs.spring.mongodb.reactive)
    //implementation(libs.mongodb.reactivestreams)
    implementation(libs.querydsl.mongodb)

    //Querydsl
    implementation(libs.querydsl.apt)

    // jackson
    implementation(libs.jackson.dataformat.xml)

    // jboss
    implementation(libs.jboss.logging)
    implementation(libs.jboss.marshalling)

    // apache
    implementation(libs.commons.lang3)

    // swagger
    implementation(libs.swagger.annotations)
    implementation(libs.springdoc.openapi.webflux.ui)

    // testContainers
    implementation(libs.testcontainers)

    // junit
    implementation(libs.kotlin.test.junit)

    // protobuf
    implementation(platform(libs.protobuf.bom))

    // grpc
    implementation(platform(libs.grpc.bom))
    implementation(libs.grpc.server.autoconfigure)
    implementation(libs.grpc.gateway)

    // redis
    implementation(libs.spring.redis.session)

    // jakarta
    implementation(libs.jakarta.annotation.api)
    implementation(libs.jakarta.validation)
    implementation(libs.log4j.jakarta.smtp)

    // kotlin
//    implementation(libs.kotlin.stdlib.common)
//    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.osgi.bundle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.plugin.noarg)
    implementation(libs.kotlin.script.runtime)
    implementation(libs.kotlin.jacoco)
    implementation(libs.coroutines.reactor)
    implementation(libs.coroutines.reactive)
    implementation(libs.kotlin.serialization.json)

    //detekt plugin
    implementation(libs.detekt.formatting)
    implementation(libs.detekt.rules.libraries)
    implementation(libs.detekt.rules.ruleauthors)

    // bucket4j
    implementation(libs.bucket4j.core)

    // jsoup
    implementation(libs.jsoup)

    compileOnly(libs.jmh.core)
    compileOnly(libs.jmh.annotations)

    testApi(libs.coroutines.test)

    testCompileOnly(libs.kotlin.test.junit)
    testRuntimeOnly(libs.testcontainers.junit.jupiter)

    //testCompileOnly("org.junit.jupiter:junit-jupiter-api")
    //testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    constraints {
        // no constraints
    }

    configurations {

        all {
            resolutionStrategy {
                failOnVersionConflict()
                preferProjectModules()
                // cache dynamic versions for 10 minutes
                cacheDynamicVersionsFor(600, "seconds")
                // don't cache changing modules at all
                cacheChangingModulesFor(0, "seconds")
            }

            exclude(group = springBootGroup, module = springExcStarterLogging)
            exclude(group = springBootGroup, module = "junit-vintage-engine")
            exclude(group = springDocGroup, module = "jackson-core")
            exclude(group = springBootGroup, module = "slf4j-api")
            exclude(group = springBootGroup, module = "jboss-logging")
            exclude(group = "${springBootGroup}:spring-boot-autoconfigure", module = "gson")
            exclude(group = "${springBootGroup}:spring-boot-starter-test", module = "json-path")
            exclude(group = "${springBootGroup}:spring-boot-starter-test", module = "org.mockito")
            exclude(group = springFasterXmlGroup, module = "jackson-databind")
            exclude(group = springBootCloudGroup, module = "com.fasterxml.jackson.dataformat")
            exclude(group = springBootCloudGroup, module = "spring-security-crypto")
            exclude(group = springBootCloudGroup, module = "com.fasterxml.jackson.core")
            exclude(group = springBootCloudGroup, module = "slf4j-api")
            exclude(group = springDevh, module = "proto-google-common-protos")
            exclude(group = springDevh, module = "error_prone_annotations")
            exclude(group = springGrpc, module = "grpc-context")
            exclude(group = springBootKafkaGroup, module = "spring-core")
            exclude(group = springBootKafkaGroup, module = "spring-beans")
            exclude(group = springBootKafkaGroup, module = "spring-context")
            exclude(group = springBootKafkaGroup, module = "spring-tx")
            exclude(group = projectReactorGroup, module = "reactor-core")
            exclude(module = "jackson-databind")
            exclude(group = springDocGroup, module = "commons-lang3")
            exclude(group = springDocGroup, module = "jackson-core")
            exclude(group = springDocGroup, module = "slf4j-api")
            exclude(group = springDocGroup, module = "jackson-datatype-jsr310")
            exclude(group = springDocGroup, module = "snakeyaml")
            exclude(group = springDocGroup, module = "spring-boot-autoconfigure")
            exclude(group = springDocGroup, module = "spring-core")
            exclude(group = springDocGroup, module = "spring-webflux")
            exclude(group = springDocGroup, module = "spring-web")
            exclude(group = springDocGroup, module = "jackson-annotations")
            exclude(group = springDocGroup, module = "jackson-databind")
            exclude(group = springDocGroup, module = "jakarta.xml.bind-api")
            exclude(group = jsonWebTokenGroup, module = "guava")
            exclude(group = "$jsonWebTokenGroup:org.json", module = "json")
            exclude(group = springDevh, module = "slf4j-api")
            exclude(group = springDevh, module = "proto-google-common-protos")
            exclude(group = springDevh, module = "error_prone_annotations")
            exclude(group = springDevh, module = "protobuf-java-util")
            exclude(group = springGrpc, module = "opencensus-api")
            exclude(group = apacheLoggingGroup, module = "jakarta-activation")
            exclude(group = apacheLoggingGroup, module = "smtp")
        }
    }
}

subprojects {

    apply {
        plugin("idea")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.gradle.test-retry")
    }

    group = rootProject.group
    version = rootProject.version

}

// new kotlin settings for kotlin 2.0
kotlin {

    kotlinDaemonJvmArgs = listOf("-Xmx486m", "-Xms256m", "-XX:+UseParallelGC")

    sourceSets.all {
        // language-version 2.0
        languageSettings {
            languageVersion = KOTLIN_LANG_VERSION
        }
    }

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(JVM_VERSION))
    }
    jvmToolchain(JVM_VERSION.toInt())

}

tasks
    .withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .configureEach {
        compilerOptions.apply {
            languageVersion.set(KOTLIN_2_0)
            apiVersion.set(KOTLIN_2_0)
        }
    }

tasks.compileJava {
    isEnabled = false
}

tasks.pluginDescriptors {
    isEnabled = false
}

tasks.compileTestJava {
    isEnabled = false
}

tasks.checkstyleMain {
    isEnabled = true
}

checkstyle {
    // will use the version declared in the catalog
    //toolVersion = libs.versions.checkstyle.get()
    //exclude("**/gen/**")
}

//graalvmNative {
//    agent {
//        defaultMode.set("standard")
//    }
//    metadataRepository {
//        enabled.set(true)
//    }
//
//    binaries {
//        binaries.all {
//            resources.autodetect()
//        }
//        testSupport.set(false)
////        named("main") {
////            javaLauncher.set(javaToolchains.launcherFor {
////                languageVersion.set(JavaLanguageVersion.of(17))
////                vendor.set(JvmVendorSpec.matching("GraalVM Community"))
////            })
////        }
//        toolchainDetection.set(false)
//    }
//}

tasks.bootJar {
    duplicatesStrategy = EXCLUDE
}

tasks.jar {
    duplicatesStrategy = EXCLUDE
}

gradle.taskGraph.whenReady {
    allTasks
        .filter { it.hasProperty("duplicatesStrategy") } // Because it's some weird decorated wrapper that I can't cast.
        .forEach {
            it.setProperty("duplicatesStrategy", "EXCLUDE")
        }
}

tasks.jar {
    manifest {
        attributes[MAIN_CLASS] = ROOT_PROJECT
        attributes[IMPLEMENTATION_TITLE] = ROOT_PROJECT
        attributes[IMPLEMENTATION_VERSION] = ROOT_PROJECT
    }

    project.version = NO_VERSION
    project.setProperty(MAIN_CLASS_NAME, ROOT_PROJECT)
    archiveBaseName.set(project.name)

}

allOpen {
    annotation("$group")
    // annotations("com.another.Annotation", "com.third.Annotation")
}

noArg {
    annotation("org.dbs.NoArgConstructor")
    invokeInitializers = true
}


//======================================================================================================================

//sourceSets.all {
//    java.setSrcDirs(listOf("$name/src"))
//    resources.setSrcDirs(listOf("$name/resources"))
//}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}


group = PROJECT_GROUP
description = PRODUCT_DESCRIPTION
version = PRODUCT_VERSION


val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = JVM_VERSION
    languageVersion = KOTLIN_2_0.version
    //freeCompilerArgs = listOf("-Xjsr305=strict", "-Xdoclint:none", "-Xlint:none", "-nowarn")
    freeCompilerArgs = listOf("-Xjsr305=strict", "-Xno-call-assertions", "-Xno-receiver-assertions", "-Xno-param-assertions")
    allWarningsAsErrors = true
    suppressWarnings = false
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*> by tasks
compileTestKotlin.compilerOptions {
//    jvmTarget = jvmVersion
//    languageVersion = KOTLIN_2_0.version
    //freeCompilerArgs = listOf("-Xjsr305=strict", "-Xdoclint:none", "-Xlint:none", "-nowarn")
}

tasks.withType<Test> {

    retry {
        maxRetries.set(10)
    }

    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)  //Code coverage will be generated every time tests run.
    finalizedBy("jacocoTestReport")
    //finalizedBy(jacocoTestReport)  // report is always generated after tests run
}



tasks.test {
    useJUnitPlatform()
//    finalizedBy tasks.jacocoTestReport  //Code coverage will be generated every time tests run.
//    finalizedBy jacocoTestReport  // report is always generated after tests run


}

jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("reportsJaCoCo"))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        html.required.set(true)
        xml.required.set(false)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.5".toBigDecimal()
            }
        }

        rule {
            enabled = false
            element = "CLASS"
            includes = listOf("org.dbs.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}

val testCoverage by tasks.registering {
    group = "verification"
    description = "Runs the unit tests with coverage"

    dependsOn(
        ":test",
        ":jacocoTestReport",
        ":jacocoTestCoverageVerification"
    )

    tasks["jacocoTestReport"].mustRunAfter(tasks["test"])
    tasks["jacocoTestCoverageVerification"].mustRunAfter(tasks["jacocoTestReport"])
}
