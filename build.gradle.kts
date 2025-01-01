import io.ktor.plugin.features.*
import net.researchgate.release.BaseScmAdapter
import net.researchgate.release.GitAdapter
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val group: String by project
val applicationId: String by project
val version: String by project

plugins {
    kotlin("jvm") version Dependencies.Versions.kotlinPluginVersion
    id("io.ktor.plugin") version Dependencies.Versions.ktorVersion
    kotlin("plugin.serialization") version Dependencies.Versions.kotlinPluginSerializationVersion
    id("net.researchgate.release") version Dependencies.Versions.releasePluginVersion
}

application {
    mainClass.set("com.pricewatcher.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("${applicationId}.jar")
    }
    docker {
        jreVersion = JavaVersion.VERSION_17
        localImageName = applicationId
        imageTag = version
        portMappings = listOf(
            DockerPortMapping(3500, 3500, DockerPortMappingProtocol.TCP)
        )
        externalRegistry = (
                DockerImageRegistry.dockerHub(
                    appName = provider { applicationId },
                    username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                    password = providers.environmentVariable("DOCKER_HUB_PASSWORD"),
                )
        )
    }
}

release {
    failOnCommitNeeded = true
    failOnUnversionedFiles = false
    failOnSnapshotDependencies = true
    revertOnFail = true

    preCommitText = "release/"
    versionPropertyFile = "gradle.properties"

    scmAdapters = listOf<Class<out BaseScmAdapter>>(GitAdapter::class.java)

    git {
        requireBranch = "master"
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
    implementation(Dependencies.koinLogger)
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

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}