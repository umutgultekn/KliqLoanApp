package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.LoanFixtures.loan
import org.junit.Assert.assertEquals
import org.junit.Test

private const val DELTA = 0.0001

class LoanProcessorTest {

    private val processor = LoanProcessor(
        mapOf(
            LoanType.PERSONAL to PersonalLoanStrategy(),
            LoanType.MORTGAGE to MortgageLoanStrategy(),
            LoanType.AUTO to AutoLoanStrategy(),
            LoanType.BUSINESS to BusinessLoanStrategy(),
        ),
    )

    @Test fun `strategy runs BEFORE the due-date decrement`() {
        // dueInDays == 1 takes the ">0" branch (+0.3). If the decrement ran first (-> 0), it would
        // instead take the small-principal branch (+0.6). Asserting +0.3 proves the ordering.
        val result = processor.process(
            loan(type = LoanType.PERSONAL, status = LoanStatus.ACTIVE, dueInDays = 1, principalAmount = 5_000.0, interestRate = 3.0),
        )
        assertEquals(3.3, result.interestRate, DELTA)
        assertEquals(0, result.dueInDays)
        assertEquals(LoanStatus.ACTIVE, result.status)
    }

    @Test fun `dueInDays zero takes the past-due branch then decrements`() {
        val result = processor.process(
            loan(type = LoanType.PERSONAL, status = LoanStatus.ACTIVE, dueInDays = 0, principalAmount = 5_000.0, interestRate = 3.0),
        )
        assertEquals(3.6, result.interestRate, DELTA)
        assertEquals(-1, result.dueInDays)
    }

    @Test fun `overdue default stage sees the post-strategy post-decrement state`() {
        // Active, small-principal, dueInDays -90: strategy adds 0.6 and stays ACTIVE; decrement -> -91;
        // the global stage then escalates to DEFAULT. Proves strategy -> decrement -> -90 ordering.
        val result = processor.process(
            loan(type = LoanType.PERSONAL, status = LoanStatus.ACTIVE, dueInDays = -90, principalAmount = 5_000.0, interestRate = 3.0),
        )
        assertEquals(3.6, result.interestRate, DELTA)
        assertEquals(-91, result.dueInDays)
        assertEquals(LoanStatus.DEFAULT, result.status)
    }

    @Test fun `terminal loans skip the strategy but are still decremented`() {
        val result = processor.process(LoanFixtures.commercialCredit) // DEFAULT, -40, 4.3
        assertEquals(4.3, result.interestRate, DELTA) // unchanged: no strategy applied
        assertEquals(-41, result.dueInDays)           // still decremented
        assertEquals(LoanStatus.DEFAULT, result.status)
    }

    @Test fun `real record Vehicle Finance stays overdue`() {
        val result = processor.process(LoanFixtures.vehicleFinance) // AUTO OVERDUE 42000 -8 3.6
        assertEquals(5.4, result.interestRate, DELTA)
        assertEquals(-9, result.dueInDays)
        assertEquals(LoanStatus.OVERDUE, result.status)
    }

    @Test fun `real record Premium Auto Lease defaults`() {
        val result = processor.process(LoanFixtures.premiumAutoLease) // AUTO OVERDUE 62000 -14 4.7
        assertEquals(6.5, result.interestRate, DELTA)
        assertEquals(-15, result.dueInDays)
        assertEquals(LoanStatus.DEFAULT, result.status)
    }

    @Test fun `processing is deliberately non-idempotent (double decrement)`() {
        val once = processor.process(LoanFixtures.consumerCredit)
        val twice = processor.process(once)
        assertEquals(once.dueInDays - 1, twice.dueInDays)
    }

    @Test fun `list processing maps every element`() {
        val input = listOf(LoanFixtures.consumerCredit, LoanFixtures.vehicleFinance, LoanFixtures.commercialCredit)
        assertEquals(input.size, processor.process(input).size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `construction fails fast when a strategy binding is missing`() {
        LoanProcessor(mapOf(LoanType.PERSONAL to PersonalLoanStrategy()))
    }
}
