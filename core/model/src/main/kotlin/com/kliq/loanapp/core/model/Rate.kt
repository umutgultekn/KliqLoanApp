package com.kliq.loanapp.core.model

/**
 * An interest rate in percent (e.g. `Rate(3.6)` == 3.6%) as a typed value, so it can't be swapped
 * with a [Money] amount and `addRate` reads as a domain operation rather than raw `Double` addition.
 */
@JvmInline
value class Rate(val percent: Double) : Comparable<Rate> {
    operator fun plus(other: Rate): Rate = Rate(percent + other.percent)
    override fun compareTo(other: Rate): Int = percent.compareTo(other.percent)

    companion object {
        val Zero = Rate(0.0)
    }
}
