package com.pricewatcher.config

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials

class Config(
    val environment: String,
    val host: String,
    val port: Int,
    val botApiKey: String,
    val awsCredentials: AwsBasicCredentials,
    val dynamoDbEndpoint: String
) {

    fun isDevEnv(): Boolean = "DEV".equals(environment, ignoreCase = true)
}