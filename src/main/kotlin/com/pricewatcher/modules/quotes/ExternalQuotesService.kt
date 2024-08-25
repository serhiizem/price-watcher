package com.pricewatcher.modules.quotes

import com.pricewatcher.config.Config
import com.pricewatcher.domain.SimpleQuote
import com.pricewatcher.util.LoggerFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ExternalQuotesService : QuotesService, KoinComponent {

    private val log = LoggerFactory.getLogger(this)
    private val config by inject<Config>()

    private val client = HttpClient(CIO) {
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

    override suspend fun getQuotes(symbols: List<String>): List<SimpleQuote> {
        log.info("Requesting quotes for symbols: $symbols")
        val requestedSymbols = symbols.joinToString(",")
        val response = client.get("quote-short/$requestedSymbols")
        val quotes = response.body<List<SimpleQuoteEntity>>().map { it.toDomain() }
        log.info("Received quotes: $quotes")
        return quotes
    }
}

@Serializable
data class SimpleQuoteEntity(
    val symbol: String,
    val price: Double,
    val volume: Double
)

fun SimpleQuoteEntity.toDomain() = SimpleQuote(symbol, price, volume)