package com.pricewatcher.persistence

import com.pricewatcher.config.Config
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

object PersistenceClientFactory : KoinComponent {

    private val config by inject<Config>()

    fun dynamoDbClient(): DynamoDbEnhancedAsyncClient = DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(
            DynamoDbAsyncClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(config.dynamoDbEndpoint))
                .credentialsProvider(StaticCredentialsProvider.create(config.awsCredentials))
                .build()
        )
        .build()
}