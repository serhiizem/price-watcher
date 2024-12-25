package com.pricewatcher.application.injection

import com.pricewatcher.application.config.Config
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import org.koin.dsl.module

object ConfigInjection {

    val koinBeans = module {
        single { Config.instance() }
        single { PrometheusMeterRegistry(PrometheusConfig.DEFAULT) }
    }
}