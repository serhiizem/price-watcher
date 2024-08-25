package com.pricewatcher.persistence

import com.pricewatcher.config.Config
import com.pricewatcher.persistence.ddl.TableCreator
import com.pricewatcher.persistence.entities.SubscriptionEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

object DynamoClientProvider : PersistenceClientProvider<DynamoDbEnhancedAsyncClient>, KoinComponent {

    private val config by inject<Config>()

    private lateinit var dynamoDbEnhancedAsyncClient: DynamoDbEnhancedAsyncClient

    override fun init() {
        dynamoDbEnhancedAsyncClient = DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(
                DynamoDbAsyncClient.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(URI.create(config.dynamoDbEndpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(config.awsCredentials))
                    .build()
            )
            .build()

        initTables()
    }

    private fun initTables() {
        if (config.isDevEnv()) {
            val tableCreator = TableCreator(
                "Subscriptions", dynamoDbEnhancedAsyncClient, SubscriptionEntity::class.java)
            tableCreator.execute()
        }
    }

    override fun get(): DynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient
}