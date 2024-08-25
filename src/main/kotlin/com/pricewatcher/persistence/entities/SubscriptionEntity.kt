package com.pricewatcher.persistence.entities

import com.pricewatcher.domain.AssetPriceSubscription
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

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