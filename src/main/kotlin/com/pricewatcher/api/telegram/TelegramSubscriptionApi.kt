package com.pricewatcher.api.telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.pricewatcher.api.PriceSubscriptionApi
import com.pricewatcher.application.config.Config
import com.pricewatcher.extensions.toAssetPriceSubscription
import com.pricewatcher.persistence.dao.SubscriptionsDao
import com.pricewatcher.util.LoggerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TelegramSubscriptionApi : PriceSubscriptionApi, KoinComponent {

    private val log = LoggerFactory.getLogger(this)
    private val config by inject<Config>()
    private val subscriptionsDao by inject<SubscriptionsDao>()

    private val bot: Bot

    init {
        log.info("Staring telegram subscription bot")
        bot = bot {
            token = config.api.botApiKey
            dispatch { text { onMessageReceived(text, message) } }
        }
        bot.startPolling().apply {
            log.info("Telegram subscription bot is polling for messages")
        }
    }

    private fun onMessageReceived(text: String, message: Message) {
        log.info("Received message: $text")
        if (text.startsWith("/notify")) {
            val chatId = message.chat.id
            val assetPriceSubscription = message.toAssetPriceSubscription()
            subscriptionsDao.save(assetPriceSubscription)
            bot.sendMessage(
                chatId = ChatId.fromId(chatId),
                text = "You will be notified when price of ${assetPriceSubscription.symbol} " +
                        "crosses ${assetPriceSubscription.priceCondition.prettyString()} " +
                        assetPriceSubscription.price.toPlainString()
            )
        }
    }

    override fun sendMessageTo(message: String, receiver: String) {
        val result = bot.sendMessage(
            chatId = ChatId.fromId(receiver.toLong()),
            text = message
        )
        result.fold({
            log.info("Message has been sent to: $receiver")
        }, {
            log.error("Error occurred when sending message to: $receiver")
        })
    }
}