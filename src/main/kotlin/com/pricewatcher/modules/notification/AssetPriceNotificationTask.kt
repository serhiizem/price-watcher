package com.pricewatcher.modules.notification

import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.modules.quotes.QuotesService
import com.pricewatcher.persistence.dao.SubscriptionsDao
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.math.BigDecimal
import kotlin.time.Duration.Companion.minutes

object AssetPriceNotificationTask : NotificationTask, KoinComponent {

    override val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    private val subscriptionsDao by inject<SubscriptionsDao>()
    private val quotesService by inject<QuotesService>()
    private val priceSubscriptionApi by inject<PriceSubscriptionApi>()

    init {
        scope.launch {
            while (isActive) {
                notifyOnMatchingConditions()
                delay(10.minutes)
            }
        }
    }

    private suspend fun notifyOnMatchingConditions() {
        val existingSubscriptions = subscriptionsDao.findAll()
        val trackedSymbols = existingSubscriptions.map { it.symbol }
        val quotes = quotesService.getQuotes(trackedSymbols)

        existingSubscriptions.forEach { subscription ->
            quotes.find {
                it.symbol == subscription.symbol
            }?.let {
                val actualAssetPrice: BigDecimal = it.price.toBigDecimal()
                val targetAssetPrice: BigDecimal = subscription.price
                val priceCondition = subscription.priceCondition

                if (priceCondition.hasMatch(actualAssetPrice, targetAssetPrice)) {
                    val message = "Price of ${it.symbol} is $actualAssetPrice " +
                            "(${priceCondition.prettyString()} the requested target price $targetAssetPrice)"
                    priceSubscriptionApi.sendMessageTo(message, subscription.originatingSource)
                }
            }
        }
    }
}
