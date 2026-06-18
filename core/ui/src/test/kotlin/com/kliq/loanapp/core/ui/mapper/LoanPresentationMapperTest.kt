package com.kliq.loanapp.core.ui.mapper

import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.testing.LoanFixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class LoanPresentationMapperTest {

    private val mapper = LoanPresentationMapper(DefaultLoanFormatter())

    @Test fun `maps a loan to a card config with semantic tones`() {
        val card = mapper.toCard(LoanFixtures.vehicleFinance) // AUTO OVERDUE 42000 -8 3.6
        assertEquals("Vehicle Finance", card.title)
        assertEquals(Tone.TypeAuto, card.typeBadge.tone)
        assertEquals(Tone.Overdue, card.statusBadge.tone)
        assertEquals(Tone.Default, card.dueTone) // negative due window
        assertEquals("8 days overdue", card.dueText)
        assertEquals("AUTO", card.typeBadge.label)
    }

    @Test fun `due text reflects remaining, today, and overdue`() {
        assertEquals("45 days remaining", mapper.toCard(LoanFixtures.loan(dueInDays = 45)).dueText)
        assertEquals("Due today", mapper.toCard(LoanFixtures.loan(dueInDays = 0)).dueText)
        assertEquals("8 days overdue", mapper.toCard(LoanFixtures.loan(dueInDays = -8)).dueText)
    }

    @Test fun `summary aggregates total, count and average`() {
        val loans = listOf(
            LoanFixtures.loan(principalAmount = 10_000.0, interestRate = 2.0),
            LoanFixtures.loan(principalAmount = 30_000.0, interestRate = 4.0),
        )
        val summary = mapper.summary(loans)
        assertEquals("2 loans in portfolio", summary.countText)
        assertEquals("$40,000", summary.totalText)
        assertEquals("Avg. interest rate: 3.0%", summary.avgRateText)
    }

    @Test fun `empty portfolio yields the empty summary`() {
        assertEquals(PortfolioSummaryUi.Empty, mapper.summary(emptyList()))
    }
}
