package com.pricewatcher.persistence.entities

import com.pricewatcher.domain.AssetPriceSubscription
import com.pricewatcher.domain.PriceCondition
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import java.util.*

@DynamoDbBean
class SubscriptionEntity {

    @get:DynamoDbPartitionKey
    var symbol: String? = null

    var condition: String? = null
    var price: Double? = null
    var subscriber: String? = null
}

fun AssetPriceSubscription.toEntity(): SubscriptionEntity {
    val entity = SubscriptionEntity()
    entity.symbol = symbol
    entity.condition = priceCondition.toString()
    entity.price = price.toDouble()
    entity.subscriber = originatingSource
    return entity
}

fun SubscriptionEntity.toDomain(): AssetPriceSubscription {
    val priceCondition = PriceCondition.valueOf(condition!!.uppercase(Locale.getDefault()))
    val priceValue = price!!.toBigDecimal()
    return AssetPriceSubscription(subscriber!!, symbol!!, priceCondition, priceValue)
}