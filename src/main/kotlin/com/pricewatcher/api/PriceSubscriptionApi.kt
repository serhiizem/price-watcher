package com.pricewatcher.api

interface PriceSubscriptionApi {
    fun sendMessageTo(message: String, receiver: String)
}