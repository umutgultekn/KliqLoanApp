package com.kliq.loanapp.core.common.format

import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Centralized numeric formatting provider — money and interest are never formatted with ad-hoc
 * `String.format` calls in screens or ViewModels (a named starter smell), but flow through here.
 */
interface LoanFormatter {
    fun money(amount: Double): String
    fun percent(rate: Double): String
}

/**
 * Default implementation. The portfolio currency is USD / [Locale.US] by explicit decision
 * (loans.json carries no currency code); swappable by providing a different [LoanFormatter].
 */
class DefaultLoanFormatter @Inject constructor() : LoanFormatter {

    private val currencyFormat: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }

    override fun money(amount: Double): String = currencyFormat.format(amount)

    override fun percent(rate: Double): String = String.format(Locale.US, "%.1f%%", rate)
}
