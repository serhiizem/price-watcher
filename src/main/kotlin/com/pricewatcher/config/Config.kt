package com.pricewatcher.config

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials

class Config(
    private val environment: String,
    val host: String,
    val port: Int,
    val botApiKey: String,
    val quoteApiKey: String,
    val awsCredentials: AwsBasicCredentials,
    val dynamoDbEndpoint: String
) {

    fun createTables(): Boolean = "DEV".equals(environment, ignoreCase = true)
}