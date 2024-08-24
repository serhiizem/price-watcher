package com.pricewatcher.extensions

import com.github.kotlintelegrambot.entities.Message
import com.pricewatcher.domain.AssetPriceSubscription
import com.pricewatcher.domain.PriceCondition

fun Message.toAssetPriceSubscription(): AssetPriceSubscription {
    val parts = text?.split(" ")!!
    val symbol = parts[1]
    val priceCondition = parts[2].let { PriceCondition.valueOf(it.uppercase()) }
    val price = parts[3].toBigDecimal()

    val chatId = chat.id.toString()

    return AssetPriceSubscription(chatId, symbol, priceCondition, price)
}