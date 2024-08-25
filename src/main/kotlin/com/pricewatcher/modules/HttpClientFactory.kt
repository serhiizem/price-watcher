package com.pricewatcher.modules

import com.pricewatcher.config.Config
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object HttpClientFactory : KoinComponent {

    private val config by inject<Config>()

    private val finDataClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "financialmodelingprep.com"
                path("api/", "v3/")
                parameters.append("apikey", config.quoteApiKey)
            }
        }
    }

    fun finDataClient() = finDataClient
}