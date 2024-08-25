package com.pricewatcher.modules.quotes

import com.pricewatcher.config.Config
import com.pricewatcher.domain.SimpleQuote
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ExternalQuotesService : QuotesService, KoinComponent {

    private val config by inject<Config>()

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        defaultRequest {
            url("https://financialmodelingprep.com/api/v3/")
        }
    }

    override suspend fun getSimpleQuote(symbol: String): SimpleQuote {
        val quoteResponse: List<SimpleQuote> =
            client.get("quote-short/$symbol?apikey=${config.quoteApiKey}").body<List<SimpleQuote>>()
        return quoteResponse[0]
    }
}