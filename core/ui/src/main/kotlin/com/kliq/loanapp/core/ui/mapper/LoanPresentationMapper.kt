package com.kliq.loanapp.core.ui.mapper

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.format.LoanFormatter
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.component.BadgeConfig
import com.kliq.loanapp.core.designsystem.component.LoanCardConfig
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject
import kotlin.math.abs

/** Formatted summary for the portfolio header card. */
@Immutable
data class PortfolioSummaryUi(
    val totalText: String,
    val countText: String,
    val avgRateText: String,
) {
    companion object {
        val Empty = PortfolioSummaryUi(totalText = "$0", countText = "0 loans", avgRateText = "0.0%")
    }
}

/**
 * The single conversion point from domain models to recomposition-stable, presentation-ready UI
 * configs. Uses [LoanFormatter] for all numeric formatting; emits semantic [Tone]s, never colors.
 *
 * Due-date phrasing is centralized here (English); production would localize it via Android plurals.
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
        rateText = "${formatter.percent(loan.interestRate)} interest",
        dueText = dueText(loan.dueInDays),
        dueTone = dueTone(loan.dueInDays),
        typeBadge = BadgeConfig(
            label = loan.type.name,
            tone = loan.type.toTone(),
            contentDescription = "Loan type: ${loan.type.name.lowercase()}",
        ),
        statusBadge = BadgeConfig(
            label = loan.status.name,
            tone = loan.status.toTone(),
            contentDescription = "Status: ${loan.status.name.lowercase()}",
        ),
    )

    fun summary(loans: List<Loan>): PortfolioSummaryUi {
        if (loans.isEmpty()) return PortfolioSummaryUi.Empty
        val total = loans.sumOf { it.principalAmount }
        val avgRate = loans.sumOf { it.interestRate } / loans.size
        return PortfolioSummaryUi(
            totalText = formatter.money(total),
            countText = "${loans.size} loans in portfolio",
            avgRateText = "Avg. interest rate: ${formatter.percent(avgRate)}",
        )
    }

    private fun dueText(dueInDays: Int): String = when {
        dueInDays > 0 -> "$dueInDays days remaining"
        dueInDays == 0 -> "Due today"
        else -> "${abs(dueInDays)} days overdue"
    }

    private fun dueTone(dueInDays: Int): Tone = when {
        dueInDays > 0 -> Tone.Active
        dueInDays == 0 -> Tone.Overdue
        else -> Tone.Default
    }
}

private fun LoanStatus.toTone(): Tone = when (this) {
    LoanStatus.ACTIVE -> Tone.Active
    LoanStatus.OVERDUE -> Tone.Overdue
    LoanStatus.DEFAULT -> Tone.Default
    LoanStatus.PAID -> Tone.Paid
}

private fun LoanType.toTone(): Tone = when (this) {
    LoanType.PERSONAL -> Tone.TypePersonal
    LoanType.MORTGAGE -> Tone.TypeMortgage
    LoanType.AUTO -> Tone.TypeAuto
    LoanType.BUSINESS -> Tone.TypeBusiness
}
