package com.pricewatcher.persistence.injection

import com.pricewatcher.persistence.PersistenceClientFactory
import com.pricewatcher.persistence.dao.SubscriptionsDao
import com.pricewatcher.persistence.dao.SubscriptionsTable
import org.koin.dsl.module
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient

object DaoInjection {
    val koinBeans = module {
        single<DynamoDbEnhancedAsyncClient> { PersistenceClientFactory.dynamoDbClient() }
        single<SubscriptionsDao> { SubscriptionsTable }
    }
}