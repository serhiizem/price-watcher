package com.pricewatcher.application.config

data class InfraConfig(
    val infraAccessKey: String,
    val infraSecretKey: String,
    val dynamoDbEndpoint: String
)