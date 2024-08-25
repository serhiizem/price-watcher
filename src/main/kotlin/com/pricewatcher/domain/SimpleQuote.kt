package com.pricewatcher.domain

import kotlinx.serialization.Serializable

@Serializable
data class SimpleQuote(
    val symbol: String,
    val price: Double,
    val volume: Double
)