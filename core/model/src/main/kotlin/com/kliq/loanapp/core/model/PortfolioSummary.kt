package com.kliq.loanapp.core.model

/** Aggregate figures shown in the portfolio summary card. Pure data — computed by the presentation layer. */
data class PortfolioSummary(
    val totalPrincipal: Double,
    val loanCount: Int,
    val averageInterestRate: Double,
) {
    companion object {
        val Empty = PortfolioSummary(totalPrincipal = 0.0, loanCount = 0, averageInterestRate = 0.0)
    }
}
