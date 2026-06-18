package com.kliq.loanapp.core.ui.mapper

import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.testing.LoanFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanPresentationMapperTest {

    private val mapper = LoanPresentationMapper(DefaultLoanFormatter())

    @Test fun `maps a loan to a card config with semantic tones`() {
        val card = mapper.toCard(LoanFixtures.vehicleFinance) // AUTO OVERDUE 42000 -8 3.6
        assertEquals("Vehicle Finance", card.title)
        assertEquals(Tone.TypeAuto, card.typeBadge.tone)
        assertEquals(Tone.Overdue, card.statusBadge.tone)
        assertEquals(Tone.Default, card.dueTone) // negative due window
        assertEquals("AUTO", card.typeBadge.label)
        assertEquals(8, (card.dueText as UiText.Plural).quantity)
    }

    @Test fun `due text reflects remaining, today, and overdue with the right quantity`() {
        assertEquals(45, (mapper.toCard(LoanFixtures.loan(dueInDays = 45)).dueText as UiText.Plural).quantity)
        assertTrue(mapper.toCard(LoanFixtures.loan(dueInDays = 0)).dueText is UiText.Resource)
        assertEquals(8, (mapper.toCard(LoanFixtures.loan(dueInDays = -8)).dueText as UiText.Plural).quantity)
    }

    @Test fun `summary aggregates total, count and average`() {
        val loans = listOf(
            LoanFixtures.loan(principalAmount = 10_000.0, interestRate = 2.0),
            LoanFixtures.loan(principalAmount = 30_000.0, interestRate = 4.0),
        )
        val summary = mapper.summary(loans)
        assertEquals("$40,000", summary.totalText)
        assertEquals(2, (summary.countText as UiText.Plural).quantity)
        assertTrue(summary.avgRateText is UiText.Resource)
    }

    @Test fun `empty portfolio yields the empty summary`() {
        assertEquals(PortfolioSummaryUi.Empty, mapper.summary(emptyList()))
    }
}
