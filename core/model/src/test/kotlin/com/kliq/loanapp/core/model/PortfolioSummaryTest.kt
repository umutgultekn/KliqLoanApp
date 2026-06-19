package com.kliq.loanapp.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

private const val DELTA = 0.0001

class PortfolioSummaryTest {

    private fun loan(principal: Double, rate: Double) =
        Loan("L", Money(principal), Rate(rate), LoanStatus.ACTIVE, dueInDays = 30, type = LoanType.PERSONAL)

    @Test fun `from sums principal, counts loans, and averages the rate`() {
        val summary = PortfolioSummary.from(listOf(loan(10_000.0, 2.0), loan(30_000.0, 4.0)))
        assertEquals(40_000.0, summary.totalPrincipal.amount, DELTA)
        assertEquals(2, summary.loanCount)
        assertEquals(3.0, summary.averageInterestRate.percent, DELTA)
    }

    @Test fun `from an empty list is Empty`() {
        assertEquals(PortfolioSummary.Empty, PortfolioSummary.from(emptyList()))
    }
}
