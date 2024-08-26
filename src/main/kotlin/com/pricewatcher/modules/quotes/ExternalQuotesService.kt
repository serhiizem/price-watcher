package com.pricewatcher.modules.quotes

import com.pricewatcher.domain.SimpleQuote
import com.pricewatcher.modules.FIN_MODEL_HTTP_CLIENT
import com.pricewatcher.util.LoggerFactory
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

object ExternalQuotesService : QuotesService, KoinComponent {

    private val log = LoggerFactory.getLogger(this)
    private val httpClient by inject<HttpClient>(qualifier = named(FIN_MODEL_HTTP_CLIENT))

    override suspend fun getQuotes(symbols: List<String>): List<SimpleQuote> {
        log.info("Requesting quotes for symbols: $symbols")
        val requestedSymbols = symbols.joinToString(",")
        val response = httpClient.get("quote-short/$requestedSymbols")
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