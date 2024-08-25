package com.pricewatcher.modules.quotes

import com.pricewatcher.domain.SimpleQuote

interface QuotesService {
    suspend fun getQuotes(symbols: List<String>): List<SimpleQuote>
}