package com.pricewatcher

import com.pricewatcher.api.injection.ApiInjection
import com.pricewatcher.config.Config
import com.pricewatcher.modules.injection.ModulesInjection
import com.pricewatcher.persistence.injection.DaoInjection
import com.typesafe.config.ConfigFactory
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {

    val config = extractConfig(HoconApplicationConfig(ConfigFactory.load()))

    embeddedServer(Netty, port = config.appPort) {
        println("Launching application on port ${config.appPort}")
        module {
            install(Koin) {
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
            module()
        }
    }.start(wait = true)
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
