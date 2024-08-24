package com.pricewatcher.domain

import java.math.BigDecimal

class AssetPriceSubscription(
    val originatingSource: String,
    val symbol: String,
    val priceCondition: PriceCondition,
    val price: BigDecimal
)