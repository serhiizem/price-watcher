@file:Suppress("ConstPropertyName")

object Dependencies {

    object Versions {
        const val shadowJarVersion = "5.0.0"
        const val kotlinPluginVersion = "1.8.0"
        const val ktorPluginVersion = "2.2.3"
        const val ktorVersion = "2.2.3"
        const val koinKtorVersion = "3.3.1"

        const val koinVersion = "3.3.3"
        const val telegramApiVersion = "6.2.0"

        const val awsBomVersion = "2.27.11"

        const val micrometerPrometheusVersion = "1.10.3"
        const val logbackVersion = "1.4.5"
        const val dotEnvVersion = "6.4.1"

        const val assertJVersion = "3.24.2"
        const val junit = "5.9.2"
        const val mockK = "1.13.4"
    }

    const val ktorNetty = "io.ktor:ktor-server-netty:${Versions.ktorVersion}"
    const val ktorLogging = "io.ktor:ktor-server-call-logging:${Versions.ktorVersion}"
    const val ktorStatusPages = "io.ktor:ktor-server-status-pages:${Versions.ktorVersion}"
    const val ktorContentNegotiation = "io.ktor:ktor-server-content-negotiation:${Versions.ktorVersion}"
    const val ktorSerialization = "io.ktor:ktor-serialization-gson:${Versions.ktorVersion}"
    const val ktorMetricsMicrometer = "io.ktor:ktor-server-metrics-micrometer:${Versions.ktorVersion}"

    const val koin = "io.insert-koin:koin-ktor:${Versions.koinKtorVersion}"
    const val telegramApi = "io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:${Versions.telegramApiVersion}"

    const val awsDynamoDb = "software.amazon.awssdk:dynamodb:${Versions.awsBomVersion}"
    const val awsDynamoDbEnhanced = "software.amazon.awssdk:dynamodb-enhanced:${Versions.awsBomVersion}"

    const val micrometerPrometheus =
        "io.micrometer:micrometer-registry-prometheus:${Versions.micrometerPrometheusVersion}"
    const val logback = "ch.qos.logback:logback-classic:${Versions.logbackVersion}"
    const val dotEnv = "io.github.cdimascio:dotenv-kotlin:${Versions.dotEnvVersion}"

    const val koinTest = "io.insert-koin:koin-test:${Versions.koinVersion}"
    const val ktorServerTest = "io.ktor:ktor-server-test-host:${Versions.ktorVersion}"
    const val assertJ = "org.assertj:assertj-core:${Versions.assertJVersion}"
    const val junit = "org.junit.jupiter:junit-jupiter:${Versions.junit}"
    const val junitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit}"
    const val mockK = "io.mockk:mockk:${Versions.mockK}"
}