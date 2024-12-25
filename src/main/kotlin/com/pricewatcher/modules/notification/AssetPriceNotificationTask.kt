package com.pricewatcher.modules.notification

import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.domain.AssetPriceSubscription
import com.pricewatcher.domain.SimpleQuote
import com.pricewatcher.modules.quotes.QuotesService
import com.pricewatcher.persistence.dao.SubscriptionsDao
import com.pricewatcher.util.LoggerFactory
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import kotlin.time.Duration.Companion.minutes

object AssetPriceNotificationTask : NotificationTask, KoinComponent {

    private val log = LoggerFactory.getLogger(this)

    override val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    private val subscriptionsDao by inject<SubscriptionsDao>()
    private val quotesService by inject<QuotesService>()
    private val priceSubscriptionApi by inject<PriceSubscriptionApi>()

    init {
        scope.launch {
            while (isActive) {
                runSubscriptionTask()
                delay(10.minutes)
            }
        }
    }

    private suspend fun runSubscriptionTask() {
        val existingSubscriptions = subscriptionsDao.findAll()
        val trackedSymbols = existingSubscriptions.map { it.symbol }
        val quotes = quotesService.getQuotes(trackedSymbols).associateBy { it.symbol }

        existingSubscriptions.forEach { subscription ->
            quotes[subscription.symbol]
                ?.let { notifyOnMatchingConditions(it, subscription) }
                ?: run { log.warn("No quote found for symbol: ${subscription.symbol}") }
        }
    }

    private fun notifyOnMatchingConditions(quote: SimpleQuote, subscription: AssetPriceSubscription) {
        val actualAssetPrice: BigDecimal = quote.price.toBigDecimal()
        val targetAssetPrice: BigDecimal = subscription.price
        val priceCondition = subscription.priceCondition

        if (priceCondition.hasMatch(actualAssetPrice, targetAssetPrice)) {
            val message = "Price of ${quote.symbol} is $actualAssetPrice " +
                    "(${priceCondition.prettyString()} the requested target price $targetAssetPrice)"
            priceSubscriptionApi.sendMessageTo(message, subscription.originatingSource)
        }
    }
}
