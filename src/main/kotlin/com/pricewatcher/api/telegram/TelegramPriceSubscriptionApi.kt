package com.pricewatcher.api.telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
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
                command("start") {
                    println("Received message: ${message.text}")
                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = "Hi there!")
                    result.fold({
                        // do something here with the response
                    }, {
                        // do something with the error
                    })
                }
            }
        }
        bot.startPolling()
    }
}