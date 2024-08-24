package com.pricewatcher.api.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.config.Config
import com.pricewatcher.util.LoggerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TelegramPriceSubscriptionApi : PriceSubscriptionApi, KoinComponent {

    private val log = LoggerFactory.getKtorLogger(this)
    private val config by inject<Config>()

    init {
        log.info("Staring telegram subscription bot")
        val bot = bot {
            token = config.botApiKey
            dispatch {
                text {
                    log.info("Received message: $text")
                    if (text.startsWith("/notify")) {
                        val chatId = message.chat.id
                        bot.sendMessage(chatId = ChatId.fromId(chatId), text = "subscribed to $text")
                    }
                }
            }
        }
        bot.startPolling().apply {
            log.info("Telegram subscription bot is polling for messages")
        }
    }
}