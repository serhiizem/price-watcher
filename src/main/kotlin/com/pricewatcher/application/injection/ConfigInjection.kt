package com.pricewatcher.application.injection

import com.pricewatcher.application.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.koin.dsl.module

object ConfigInjection {

    val koinBeans = module {
        single { Config.fromHocon(HoconApplicationConfig(ConfigFactory.load())) }
        single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
    }
}