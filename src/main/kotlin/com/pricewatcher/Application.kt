package com.pricewatcher

import com.pricewatcher.api.injection.ApiInjection
import com.pricewatcher.application.config.Config
import com.pricewatcher.application.injection.ConfigInjection
import com.pricewatcher.application.plugins.configureHttp
import com.pricewatcher.application.plugins.configureMonitoring
import com.pricewatcher.application.plugins.configureRouting
import com.pricewatcher.modules.injection.ModulesInjection
import com.pricewatcher.persistence.injection.DaoInjection
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin

fun main(args: Array<String>) {

    val koin = startKoin {
        modules(
            ConfigInjection.koinBeans,
            ApiInjection.koinBeans,
            ModulesInjection.koinBeans,
            DaoInjection.koinBeans
        )
    }

    val config = koin.koin.get<Config>()

    fun startServer() = embeddedServer(Netty, port = config.appPort) {
        println("Launching application on port ${config.appPort}")
        configure()
    }.start(wait = true)

    startServer()
}

fun Application.configure() {
    configureHttp()
    configureMonitoring()
    configureRouting()
}
