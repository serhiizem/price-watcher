package com.pricewatcher.config

class Config(
    private val environment: String,
    val host: String,
    val port: Int,
    val botApiKey: String,
    val quoteApiKey: String,
    val accessKey: String,
    val secretKey: String,
    val dynamoDbEndpoint: String
) {

    fun createTables(): Boolean = "DEV".equals(environment, ignoreCase = true)
}