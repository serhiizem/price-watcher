package com.pricewatcher.persistence.dao

import com.pricewatcher.config.Config
import com.pricewatcher.domain.AssetPriceSubscription
import com.pricewatcher.persistence.ddl.TableCreator
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

    private val config by inject<Config>()
    private val asyncClient by inject<DynamoDbEnhancedAsyncClient>()

    init {
        if (config.createTables()) {
            val tableCreator = TableCreator(
                TABLE_NAME, asyncClient, SubscriptionEntity::class.java
            )
            tableCreator.execute()
        }
    }

    private val table: DynamoDbAsyncTable<SubscriptionEntity> = asyncClient
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