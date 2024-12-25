package com.pricewatcher

import com.pricewatcher.api.injection.ApiInjection
import com.pricewatcher.application.config.Config
import com.pricewatcher.modules.injection.ModulesInjection
import com.pricewatcher.persistence.injection.DaoInjection
import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main(args: Array<String>) {

    val config = extractConfig(HoconApplicationConfig(ConfigFactory.load()))

    startKoin {
        modules(
            module {
                single { config }
                single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
            },
            ApiInjection.koinBeans,
            ModulesInjection.koinBeans,
            DaoInjection.koinBeans
        )
    }

    fun startServer() = embeddedServer(Netty, port = config.appPort) {
        println("Launching application on port ${config.appPort}")
        plugins()
    }.start(wait = true)

    startServer()
}

fun extractConfig(hoconConfig: HoconApplicationConfig): Config {
    val dotenv = dotenv { ignoreIfMissing = true }
    val environment = dotenv["ENVIRONMENT"] ?: handleDefaultEnvironment()
    val botApiKey = dotenv["TELEGRAM_API_KEY"]
    val quoteApiKey = dotenv["QUOTE_API_KEY"]
    val accessKey = dotenv["AWS_ACCESS_KEY"]
    val secretAccessKey = dotenv["AWS_SECRET_ACCESS_KEY"]
    val dynamoDbEndpoint = dotenv["DYNAMO_ENDPOINT"]

    val hoconEnvironment = hoconConfig.config("ktor.deployment.$environment")
    val port = Integer.parseInt(hoconEnvironment.property("port").getString())

    return Config(
        environment, port, botApiKey, quoteApiKey,
        accessKey, secretAccessKey, dynamoDbEndpoint
    )
}

fun handleDefaultEnvironment(): String {
    println("Falling back to default environment 'dev'")
    return "dev"
}
