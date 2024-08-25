package com.pricewatcher.modules.quotes

import com.pricewatcher.domain.SimpleQuote

interface QuotesService {
    suspend fun getSimpleQuote(symbol: String): SimpleQuote
}