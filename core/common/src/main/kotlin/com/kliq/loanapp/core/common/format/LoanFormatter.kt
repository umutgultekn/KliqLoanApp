package com.kliq.loanapp.core.common.format

import com.kliq.loanapp.core.model.Money
import com.kliq.loanapp.core.model.Rate
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Centralized numeric formatting — money/percent never via ad-hoc `String.format` in screens/VMs.
 * Takes the [Money]/[Rate] value types and unwraps them at this single boundary.
 */
interface LoanFormatter {
    fun money(amount: Money): String
    fun percent(rate: Rate): String
}

/** Default impl: USD / [Locale.US] (loans.json has no currency code); swap by providing another. */
class DefaultLoanFormatter @Inject constructor() : LoanFormatter {

    private val currencyFormat: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale.US).apply { maximumFractionDigits = 0 }

    override fun money(amount: Money): String = currencyFormat.format(amount.amount)

    override fun percent(rate: Rate): String = String.format(Locale.US, "%.1f%%", rate.percent)
}
