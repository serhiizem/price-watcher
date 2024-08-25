package com.pricewatcher.modules.injection

import com.pricewatcher.modules.HttpClientFactory
import com.pricewatcher.modules.notification.AssetPriceNotificationTask
import com.pricewatcher.modules.notification.NotificationTask
import com.pricewatcher.modules.quotes.ExternalQuotesService
import com.pricewatcher.modules.quotes.QuotesService
import io.ktor.client.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

object ModulesInjection {
    val koinBeans = module {
        single<HttpClient>(named("finDataHttpClient")) { HttpClientFactory.finDataClient() }
        single<QuotesService> { ExternalQuotesService }
        single<NotificationTask>(createdAtStart = true) { AssetPriceNotificationTask }
    }
}