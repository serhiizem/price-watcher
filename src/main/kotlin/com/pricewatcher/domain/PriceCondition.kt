package com.pricewatcher.domain

import java.math.BigDecimal
import java.util.*

enum class PriceCondition {
    BELOW {
        override fun hasMatch(
            actualPrice: BigDecimal,
            targetPrice: BigDecimal
        ): Boolean = actualPrice < targetPrice
    },
    ABOVE {
        override fun hasMatch(
            actualPrice: BigDecimal,
            targetPrice: BigDecimal
        ): Boolean = actualPrice > targetPrice
    };

    abstract fun hasMatch(actualPrice: BigDecimal, targetPrice: BigDecimal): Boolean

    fun prettyString(): String = this.toString().lowercase(Locale.getDefault())
}