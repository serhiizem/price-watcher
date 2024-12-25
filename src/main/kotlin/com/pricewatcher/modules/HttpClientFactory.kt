package com.pricewatcher.modules

import com.pricewatcher.application.config.Config
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val FIN_MODEL_HTTP_CLIENT = "finModelHttpClient"

object HttpClientFactory : KoinComponent {

    private val config by inject<Config>()

    fun finModelClient(): HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "financialmodelingprep.com"
                path("api/", "v3/")
                parameters.append("apikey", config.api.quoteApiKey)
            }
        }
    }
}