package com.pricewatcher.persistence

interface PersistenceClientProvider<T> {
    fun init()
    fun get(): T
}