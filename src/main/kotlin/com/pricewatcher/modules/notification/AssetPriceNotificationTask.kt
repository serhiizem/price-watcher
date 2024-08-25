package com.pricewatcher.modules.notification

import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.modules.quotes.QuotesService
import com.pricewatcher.persistence.dao.SubscriptionsDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AssetPriceNotificationTask : NotificationTask, KoinComponent {

    override val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    private val subscriptionsDao by inject<SubscriptionsDao>()
    private val quotesService by inject<QuotesService>()
    private val priceSubscriptionApi by inject<PriceSubscriptionApi>()

    init {
        scope.launch {
            val existingSubscriptions = subscriptionsDao.findAll()
            val trackedSymbols = existingSubscriptions.map { it.symbol }
            val quotes = quotesService.getQuotes(trackedSymbols)

            existingSubscriptions.forEach { subscription ->
                val message = "You are subscribed to ${subscription.symbol}. Quotes: $quotes"
                priceSubscriptionApi.sendMessageTo(message, subscription.originatingSource)
            }
        }
    }
}
