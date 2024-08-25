package com.pricewatcher.api.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.config.Config
import com.pricewatcher.extensions.toAssetPriceSubscription
import com.pricewatcher.persistence.dao.SubscriptionsDao
import com.pricewatcher.util.LoggerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TelegramSubscriptionApi : PriceSubscriptionApi, KoinComponent {

    private val log = LoggerFactory.getLogger(this)
    private val config by inject<Config>()
    private val subscriptionsDao by inject<SubscriptionsDao>()

    init {
        log.info("Staring telegram subscription bot")
        val bot = bot {
            token = config.botApiKey
            dispatch {
                text {
                    log.info("Received message: $text")
                    if (text.startsWith("/notify")) {
                        val chatId = message.chat.id
                        val assetPriceSubscription = message.toAssetPriceSubscription()
                        subscriptionsDao.save(assetPriceSubscription)
                        bot.sendMessage(chatId = ChatId.fromId(chatId), text = "subscribed to $assetPriceSubscription")
                    }
                }
            }
        }
        bot.startPolling().apply {
            log.info("Telegram subscription bot is polling for messages")
        }
    }
}