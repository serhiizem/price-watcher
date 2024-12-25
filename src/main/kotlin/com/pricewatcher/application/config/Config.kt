package com.pricewatcher.application.config

data class Config(
    private val environment: String,
    val appPort: Int,
    val botApiKey: String,
    val quoteApiKey: String,
    val infraAccessKey: String,
    val infraSecretKey: String,
    val dynamoDbEndpoint: String
) {

    fun createTables(): Boolean = false
}