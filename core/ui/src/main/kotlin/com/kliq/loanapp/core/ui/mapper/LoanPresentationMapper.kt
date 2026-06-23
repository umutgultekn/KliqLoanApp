package com.kliq.loanapp.core.ui.mapper

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.format.LoanFormatter
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioSummary
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
        // Neutral placeholder for the pre-content default state only. Every RENDERED summary —
        // including the empty portfolio — is produced by summary() through the formatter, so no
        // currency/percent string is ever hardcoded here.
        val Empty = PortfolioSummaryUi(totalText = "", countText = UiText.Empty, avgRateText = UiText.Empty)
    }
}

/**
 * The single domain→UI conversion point. Numbers go through [LoanFormatter]; prose through [UiText]
 * resources/plurals, so output is recomposition-stable and localizable.
 */
class LoanPresentationMapper @Inject constructor(
    private val formatter: LoanFormatter,
) {
    fun toCards(loans: List<Loan>): ImmutableList<LoanCardConfig> =
        loans.map(::toCard).toImmutableList()

    fun toCard(loan: Loan): LoanCardConfig = LoanCardConfig(
        // loans.json has no real id; the list key is derived from fields processing never mutates
        // (name/type/principal) — stable and unique for this fixture. Use a real Loan.id once one exists.
        id = "${loan.name}|${loan.type}|${loan.principalAmount.amount}",
        title = loan.name,
        amountText = formatter.money(loan.principalAmount),
        rateText = UiText.res(R.string.kliq_loan_rate, formatter.percent(loan.interestRate)),
        dueText = dueText(loan.dueInDays),
        dueTone = dueTone(loan.dueInDays),
        typeBadge = loan.type.toBadgeConfig(),
        statusBadge = loan.status.toBadgeConfig(),
    )

    /** Formats the domain [PortfolioSummary] — aggregation math lives in the model, numbers via [formatter]. */
    fun summary(summary: PortfolioSummary): PortfolioSummaryUi = PortfolioSummaryUi(
        totalText = formatter.money(summary.totalPrincipal),
        countText = UiText.plural(R.plurals.kliq_portfolio_loan_count, summary.loanCount, summary.loanCount),
        avgRateText = UiText.res(R.string.kliq_portfolio_avg_rate, formatter.percent(summary.averageInterestRate)),
    )

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
