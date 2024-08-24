package com.pricewatcher.persistence.dynamodb.entities

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
class Subscriptions {

    @get:DynamoDbPartitionKey
    var pk: String? = null
}