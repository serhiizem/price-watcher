package com.pricewatcher.config

class Config(
    private val environment: String,
    val appPort: Int,
    val botApiKey: String,
    val quoteApiKey: String,
    val infraAccessKey: String,
    val infraSecretKey: String,
    val dynamoDbEndpoint: String
) {

    fun createTables(): Boolean = "DEV".equals(environment, ignoreCase = true)
}