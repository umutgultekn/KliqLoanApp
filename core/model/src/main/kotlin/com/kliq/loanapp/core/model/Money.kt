package com.kliq.loanapp.core.model

/**
 * A loan principal as a typed value (not a bare [Double]) so it can't be mixed up with a rate or
 * count. Double-backed on purpose — the domain only sums/compares money, never multiplies/divides,
 * so BigDecimal would be over-engineering.
 */
@JvmInline
value class Money(val amount: Double) : Comparable<Money> {
    operator fun plus(other: Money): Money = Money(amount + other.amount)
    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    companion object {
        val Zero = Money(0.0)
    }
}
