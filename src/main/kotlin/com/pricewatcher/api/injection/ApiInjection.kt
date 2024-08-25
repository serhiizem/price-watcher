package com.pricewatcher.api.injection

import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.api.telegram.TelegramSubscriptionApi
import org.koin.dsl.module

object ApiInjection {

    val koinBeans = module {
        single<PriceSubscriptionApi>(createdAtStart = true) { TelegramSubscriptionApi }
    }
}