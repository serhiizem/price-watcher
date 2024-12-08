import io.ktor.plugin.features.*

group = AppConfig.group
version = AppConfig.versionName

plugins {
    kotlin("jvm") version Dependencies.Versions.kotlinPluginVersion
    id("io.ktor.plugin") version Dependencies.Versions.ktorPluginVersion
    kotlin("plugin.serialization") version Dependencies.Versions.kotlinPluginSerializationVersion
    id("com.github.johnrengelman.shadow") version Dependencies.Versions.shadowJarVersion
}

application {
    mainClass.set("com.pricewatcher.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("${AppConfig.applicationId}.jar")
    }
    docker {
        jreVersion = JavaVersion.VERSION_17
        localImageName = AppConfig.applicationId
        imageTag = AppConfig.versionName
        portMappings = listOf(
            DockerPortMapping(3500, 3500, DockerPortMappingProtocol.TCP))
        externalRegistry = (
            DockerImageRegistry.dockerHub(
                appName = provider { AppConfig.applicationId },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD"),
            )
        )
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Dependencies.ktorNetty)
    implementation(Dependencies.ktorContentNegotiation)
    implementation(Dependencies.ktorSerialization)
    implementation(Dependencies.ktorLogging)
    implementation(Dependencies.ktorStatusPages)
    implementation(Dependencies.ktorMetricsMicrometer)
    implementation(Dependencies.ktorClientCore)
    implementation(Dependencies.ktorClientEngine)
    implementation(Dependencies.ktorClientContentNegotiation)
    implementation(Dependencies.ktorSerializationJson)
    implementation(Dependencies.koin)
    implementation(Dependencies.telegramApi)

    implementation(Dependencies.awsDynamoDb)
    implementation(Dependencies.awsDynamoDbEnhanced)

    implementation(Dependencies.logback)
    implementation(Dependencies.micrometerPrometheus)
    implementation(Dependencies.dotEnv)

    testImplementation(Dependencies.koinTest)
    testImplementation(Dependencies.ktorServerTest)
    testImplementation(Dependencies.assertJ)
    testImplementation(Dependencies.junit)
    testRuntimeOnly(Dependencies.junitEngine)
    testImplementation(Dependencies.mockK)
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    shadowJar {
        version = "0.9"
    }
}