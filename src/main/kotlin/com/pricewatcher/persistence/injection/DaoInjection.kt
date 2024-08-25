package com.pricewatcher.persistence.injection

import com.pricewatcher.persistence.dao.SubscriptionsDao
import com.pricewatcher.persistence.dao.SubscriptionsTable
import org.koin.dsl.module

object DaoInjection {
    val koinBeans = module {
        single<SubscriptionsDao> { SubscriptionsTable }
    }
}