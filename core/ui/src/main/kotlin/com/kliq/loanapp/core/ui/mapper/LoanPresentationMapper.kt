package com.kliq.loanapp.core.ui.mapper

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.format.LoanFormatter
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.ui.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject
import kotlin.math.abs

/** Formatted summary for the portfolio header card. */
@Immutable
data class PortfolioSummaryUi(
    val totalText: String,
    val countText: UiText,
    val avgRateText: UiText,
) {
    companion object {
        val Empty = PortfolioSummaryUi(
            totalText = "$0",
            countText = UiText.plural(R.plurals.kliq_portfolio_loan_count, 0, 0),
            avgRateText = UiText.res(R.string.kliq_portfolio_avg_rate, "0.0%"),
        )
    }
}

/**
 * The single conversion point from domain models to recomposition-stable, presentation-ready UI
 * configs. Numeric formatting goes through [LoanFormatter]; user-facing prose goes through
 * [UiText] resources/plurals (resolved in composition) so it is localizable and correctly pluralized.
 */
class LoanPresentationMapper @Inject constructor(
    private val formatter: LoanFormatter,
) {
    fun toCards(loans: List<Loan>): ImmutableList<LoanCardConfig> =
        loans.map(::toCard).toImmutableList()

    fun toCard(loan: Loan): LoanCardConfig = LoanCardConfig(
        id = loan.name,
        title = loan.name,
        amountText = formatter.money(loan.principalAmount),
        rateText = UiText.res(R.string.kliq_loan_rate, formatter.percent(loan.interestRate)),
        dueText = dueText(loan.dueInDays),
        dueTone = dueTone(loan.dueInDays),
        typeBadge = loan.type.toBadgeConfig(),
        statusBadge = loan.status.toBadgeConfig(),
    )

    fun summary(loans: List<Loan>): PortfolioSummaryUi {
        if (loans.isEmpty()) return PortfolioSummaryUi.Empty
        val total = loans.sumOf { it.principalAmount }
        val avgRate = loans.sumOf { it.interestRate } / loans.size
        return PortfolioSummaryUi(
            totalText = formatter.money(total),
            countText = UiText.plural(R.plurals.kliq_portfolio_loan_count, loans.size, loans.size),
            avgRateText = UiText.res(R.string.kliq_portfolio_avg_rate, formatter.percent(avgRate)),
        )
    }

    private fun dueText(dueInDays: Int): UiText = when {
        dueInDays > 0 -> UiText.plural(R.plurals.kliq_loan_due_remaining, dueInDays, dueInDays)
        dueInDays == 0 -> UiText.res(R.string.kliq_loan_due_today)
        else -> UiText.plural(R.plurals.kliq_loan_due_overdue, abs(dueInDays), abs(dueInDays))
    }

    private fun dueTone(dueInDays: Int): Tone = when {
        dueInDays > 0 -> Tone.Active
        dueInDays == 0 -> Tone.Overdue
        else -> Tone.Default
    }
}
