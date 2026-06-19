package com.kliq.loanapp.core.model

/**
 * Aggregate figures for the portfolio summary card. The aggregation is a domain calculation, so it
 * lives here ([from]) — not in the presentation mapper — keeping money/rate math in the pure,
 * unit-tested model and leaving the mapper a pure formatter.
 */
data class PortfolioSummary(
    val totalPrincipal: Money,
    val loanCount: Int,
    val averageInterestRate: Rate,
) {
    companion object {
        val Empty = PortfolioSummary(Money.Zero, loanCount = 0, Rate.Zero)

        /** Total principal, count, and mean interest rate across [loans]. */
        fun from(loans: List<Loan>): PortfolioSummary {
            if (loans.isEmpty()) return Empty
            val total = loans.fold(Money.Zero) { acc, loan -> acc + loan.principalAmount }
            val averageRate = Rate(loans.sumOf { it.interestRate.percent } / loans.size)
            return PortfolioSummary(total, loans.size, averageRate)
        }
    }
}
