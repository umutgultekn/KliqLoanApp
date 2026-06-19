package com.kliq.loanapp.core.common.format

import com.kliq.loanapp.core.model.Money
import com.kliq.loanapp.core.model.Rate
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Centralized numeric formatting provider — money and interest are never formatted with ad-hoc
 * `String.format` calls in screens or ViewModels (a named starter smell), but flow through here.
 * Takes the [Money]/[Rate] value types and unwraps them at this single boundary.
 */
interface LoanFormatter {
    fun money(amount: Money): String
    fun percent(rate: Rate): String
}

/**
 * Default implementation. The portfolio currency is USD / [Locale.US] by explicit decision
 * (loans.json carries no currency code); swappable by providing a different [LoanFormatter].
 */
class DefaultLoanFormatter @Inject constructor() : LoanFormatter {

    private val currencyFormat: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }

    override fun money(amount: Money): String = currencyFormat.format(amount.amount)

    override fun percent(rate: Rate): String = String.format(Locale.US, "%.1f%%", rate.percent)
}
