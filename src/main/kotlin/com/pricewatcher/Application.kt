package com.pricewatcher

import com.pricewatcher.api.injection.ApiInjection
import com.pricewatcher.config.Config
import com.pricewatcher.modules.injection.ModulesInjection
import com.pricewatcher.persistence.DynamoClientProvider
import com.pricewatcher.persistence.PersistenceClientProvider
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient

fun main(args: Array<String>) {

    val config = extractConfig(HoconApplicationConfig(ConfigFactory.load()))

    embeddedServer(Netty, port = config.port) {
        println("Starting instance in ${config.host}:${config.port}")
        module {
            install(Koin) {
                modules(
                    module {
                        single { config }
                        single<PersistenceClientProvider<DynamoDbEnhancedAsyncClient>> { DynamoClientProvider }
                        single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
                    },
                    ApiInjection.koinBeans,
                    ModulesInjection.koinBeans,
                    DaoInjection.koinBeans
                )
            }
            main()
        }
    }.start(wait = true)
}

fun Application.main() {
    module()
}

fun extractConfig(hoconConfig: HoconApplicationConfig): Config {
    val dotenv = dotenv()
    val environment = dotenv["ENVIRONMENT"] ?: handleDefaultEnvironment()
    val botApiKey = dotenv["TELEGRAM_API_KEY"]
    val awsCredentials = AwsBasicCredentials.create(dotenv["AWS_ACCESS_KEY"], dotenv["AWS_SECRET_ACCESS_KEY"])
    val dynamoDbEndpoint = dotenv["DYNAMO_DB_ENDPOINT"]

    val hoconEnvironment = hoconConfig.config("ktor.deployment.$environment")
    val host = hoconEnvironment.property("host").getString()
    val port = Integer.parseInt(hoconEnvironment.property("port").getString())

    return Config(environment, host, port, botApiKey, awsCredentials, dynamoDbEndpoint)
}

fun handleDefaultEnvironment(): String {
    println("Falling back to default environment 'dev'")
    return "dev"
}
