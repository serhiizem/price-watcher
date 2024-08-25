package com.pricewatcher.persistence.dao

import com.pricewatcher.domain.AssetPriceSubscription
import com.pricewatcher.persistence.PersistenceClientProvider
import com.pricewatcher.persistence.entities.SubscriptionEntity
import com.pricewatcher.persistence.entities.toEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest

private const val TABLE_NAME = "Subscriptions"

object SubscriptionsTable : SubscriptionsDao, KoinComponent {

    private val dynamoDbClient by inject<PersistenceClientProvider<DynamoDbEnhancedAsyncClient>>()

    private val table: DynamoDbAsyncTable<SubscriptionEntity> = dynamoDbClient.get()
        .table(TABLE_NAME, TableSchema.fromBean(SubscriptionEntity::class.java))

    override fun save(subscription: AssetPriceSubscription) {
        val putItemRequest = PutItemEnhancedRequest.builder(SubscriptionEntity::class.java)
            .item(subscription.toEntity())
            .build()

        table.putItem(putItemRequest)
    }
}

interface SubscriptionsDao {
    fun save(subscription: AssetPriceSubscription)
}