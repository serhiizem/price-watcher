package com.pricewatcher.application.plugins

import com.pricewatcher.modules.metrics.metrics
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    install(Routing) {
        metrics()
    }

}