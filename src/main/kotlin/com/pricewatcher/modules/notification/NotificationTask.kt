package com.pricewatcher.modules.notification

import kotlinx.coroutines.CoroutineScope

interface NotificationTask {
    val scope: CoroutineScope
}