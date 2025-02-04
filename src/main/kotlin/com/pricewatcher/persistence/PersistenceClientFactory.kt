package com.pricewatcher.persistence

import com.pricewatcher.application.config.Config
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.net.URI

object PersistenceClientFactory : KoinComponent {

    private val config by inject<Config>()

    fun dynamoDbClient(): DynamoDbEnhancedAsyncClient {
        val (infraAccessKey, infraSecretKey, dynamoDbEndpoint) = config.infra;
        val awsCredentials = AwsBasicCredentials.create(infraAccessKey, infraSecretKey)

        return DynamoDbEnhancedAsyncClient.builder()
            .dynamoDbClient(
                DynamoDbAsyncClient.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(URI.create(dynamoDbEndpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build()
            )
            .build()
    }
}