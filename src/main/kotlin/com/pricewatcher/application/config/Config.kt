package com.pricewatcher.application.config

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.config.*

data class Config(
    private val environment: String,
    val appPort: Int,
    val api: ApiConfig,
    val infra: InfraConfig
) {
    fun createTables(): Boolean = false

    companion object {
        fun fromHocon(hoconConfig: HoconApplicationConfig): Config {
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
                environment, port,
                ApiConfig(botApiKey, quoteApiKey),
                InfraConfig(accessKey, secretAccessKey, dynamoDbEndpoint)
            )
        }

        private fun handleDefaultEnvironment(): String {
            println("Falling back to default environment 'dev'")
            return "dev"
        }
    }
}