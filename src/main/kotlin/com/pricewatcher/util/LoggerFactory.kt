package com.pricewatcher.util

import io.ktor.util.logging.*

class LoggerFactory {

    companion object {
        fun getLogger(target: Any): org.slf4j.Logger {
            return KtorSimpleLogger(target::javaClass.get().simpleName)
        }
    }
}