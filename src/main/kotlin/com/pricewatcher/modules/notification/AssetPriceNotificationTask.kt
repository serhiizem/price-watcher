package com.pricewatcher.modules.notification

import com.pricewatcher.persistence.dao.SubscriptionsDao
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
    private val subscriptionsDao by inject<SubscriptionsDao>()

    init {
        scope.launch {
            val existingSubscriptions = subscriptionsDao.findAll()
            log.info("Found ${existingSubscriptions.size} subscriptions")
        }
    }
}
