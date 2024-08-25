package com.pricewatcher.modules.notification

import com.pricewatcher.modules.quotes.QuotesService
import com.pricewatcher.persistence.dao.SubscriptionsDao
import com.pricewatcher.util.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AssetPriceNotificationTask : NotificationTask, KoinComponent {

    private val log = LoggerFactory.getLogger(this)

    override val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    private val subscriptionsDao by inject<SubscriptionsDao>()
    private val quotesService by inject<QuotesService>()

    init {
        scope.launch {
            val existingSubscriptions = subscriptionsDao.findAll()
            val trackedSymbols = existingSubscriptions.map { it.symbol }
            val quotes = quotesService.getQuotes(trackedSymbols)
            log.info("TODO: further logic with received quotes: $quotes")
        }
    }
}
