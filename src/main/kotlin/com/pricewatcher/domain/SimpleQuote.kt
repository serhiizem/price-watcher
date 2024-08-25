package com.pricewatcher.domain

data class SimpleQuote(
    val symbol: String,
    val price: Double,
    val volume: Double
)