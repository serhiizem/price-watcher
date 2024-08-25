package com.pricewatcher.modules.notification

import com.pricewatcher.modules.quotes.QuotesService
import com.pricewatcher.util.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AssetPriceNotificationTask : NotificationTask, KoinComponent {

    override val scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    private val log = LoggerFactory.getLogger(this)
    private val quotesService by inject<QuotesService>()

    init {
        scope.launch {
            log.info("Launching coroutine task, fetching simple quote")
            val simpleQuote = quotesService.getSimpleQuote("AAPL")
            log.info("Received simple quote: {}", simpleQuote)
        }
    }
}
