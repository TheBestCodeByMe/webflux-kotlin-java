import dsl.Dependencies.Projects.ACTORS_API
import dsl.Dependencies.Projects.APPLICATION_CORE_API
import dsl.Dependencies.Projects.KAFKA_API
import dsl.Dependencies.Projects.STORE_API

plugins {
    idea
}

dependencies {
    api(project(APPLICATION_CORE_API))
    api(project(KAFKA_API))
    implementation(libs.kotlin.stdlib.jdk8)
}
description = "auth-server-api"
