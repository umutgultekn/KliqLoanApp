package com.kliq.loanapp.core.model

/**
 * A loan principal as a typed value instead of a bare [Double]. Makes the domain self-documenting
 * and prevents mixing an amount with a rate or a raw count at a call site. Kept thin (Double-backed,
 * inline) on purpose — the domain never multiplies/divides money, so a full BigDecimal type would be
 * over-engineering here; the value is in the type, not in decimal precision.
 */
@JvmInline
value class Money(val amount: Double) : Comparable<Money> {
    operator fun plus(other: Money): Money = Money(amount + other.amount)
    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    companion object {
        val Zero = Money(0.0)
    }
}
