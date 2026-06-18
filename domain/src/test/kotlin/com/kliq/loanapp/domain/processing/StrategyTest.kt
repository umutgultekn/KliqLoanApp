package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.testing.LoanFixtures.loan
import org.junit.Assert.assertEquals
import org.junit.Test

private const val DELTA = 0.0001

class PersonalLoanStrategyTest {
    private val strategy = PersonalLoanStrategy()

    @Test fun `active and not yet due adds 0_3`() {
        val result = strategy.process(loan(type = LoanType.PERSONAL, status = LoanStatus.ACTIVE, dueInDays = 45, interestRate = 2.9))
        assertEquals(3.2, result.interestRate, DELTA)
        assertEquals(LoanStatus.ACTIVE, result.status)
    }

    @Test fun `active past due with large principal escalates to overdue and adds 1_2`() {
        val result = strategy.process(loan(status = LoanStatus.ACTIVE, dueInDays = -5, principalAmount = 15_000.0, interestRate = 3.0))
        assertEquals(4.2, result.interestRate, DELTA)
        assertEquals(LoanStatus.OVERDUE, result.status)
    }

    @Test fun `active past due with small principal adds 0_6 and stays active`() {
        val result = strategy.process(loan(status = LoanStatus.ACTIVE, dueInDays = -5, principalAmount = 5_000.0, interestRate = 3.0))
        assertEquals(3.6, result.interestRate, DELTA)
        assertEquals(LoanStatus.ACTIVE, result.status)
    }

    @Test fun `overdue with large principal defaults`() {
        val result = strategy.process(loan(status = LoanStatus.OVERDUE, principalAmount = 25_000.0, interestRate = 3.0))
        assertEquals(4.5, result.interestRate, DELTA)
        assertEquals(LoanStatus.DEFAULT, result.status)
    }

    @Test fun `overdue with small principal stays overdue`() {
        val result = strategy.process(loan(status = LoanStatus.OVERDUE, principalAmount = 15_000.0, interestRate = 3.0))
        assertEquals(LoanStatus.OVERDUE, result.status)
    }

    @Test fun `does not mutate input`() {
        val input = loan(status = LoanStatus.ACTIVE, dueInDays = 45, interestRate = 2.9)
        strategy.process(input)
        assertEquals(2.9, input.interestRate, DELTA)
        assertEquals(LoanStatus.ACTIVE, input.status)
    }
}

class MortgageLoanStrategyTest {
    private val strategy = MortgageLoanStrategy()

    @Test fun `active not due adds 0_1`() {
        val result = strategy.process(loan(type = LoanType.MORTGAGE, status = LoanStatus.ACTIVE, dueInDays = 100, interestRate = 1.7))
        assertEquals(1.8, result.interestRate, DELTA)
        assertEquals(LoanStatus.ACTIVE, result.status)
    }

    @Test fun `active past due becomes overdue`() {
        val result = strategy.process(loan(type = LoanType.MORTGAGE, status = LoanStatus.ACTIVE, dueInDays = -1, interestRate = 1.7))
        assertEquals(2.1, result.interestRate, DELTA)
        assertEquals(LoanStatus.OVERDUE, result.status)
    }

    @Test fun `overdue beyond minus 60 defaults`() {
        val result = strategy.process(loan(type = LoanType.MORTGAGE, status = LoanStatus.OVERDUE, dueInDays = -61, interestRate = 2.0))
        assertEquals(2.8, result.interestRate, DELTA)
        assertEquals(LoanStatus.DEFAULT, result.status)
    }

    @Test fun `overdue at minus 60 boundary stays overdue`() {
        val result = strategy.process(loan(type = LoanType.MORTGAGE, status = LoanStatus.OVERDUE, dueInDays = -60, interestRate = 2.0))
        assertEquals(LoanStatus.OVERDUE, result.status)
    }
}

class AutoLoanStrategyTest {
    private val strategy = AutoLoanStrategy()

    @Test fun `active not due adds 0_4`() {
        val result = strategy.process(loan(type = LoanType.AUTO, status = LoanStatus.ACTIVE, dueInDays = 200, interestRate = 3.9))
        assertEquals(4.3, result.interestRate, DELTA)
    }

    @Test fun `overdue under threshold stays overdue (Vehicle Finance)`() {
        val result = strategy.process(loan(type = LoanType.AUTO, status = LoanStatus.OVERDUE, principalAmount = 42_000.0, interestRate = 3.6))
        assertEquals(5.4, result.interestRate, DELTA)
        assertEquals(LoanStatus.OVERDUE, result.status)
    }

    @Test fun `overdue over threshold defaults (Premium Auto Lease)`() {
        val result = strategy.process(loan(type = LoanType.AUTO, status = LoanStatus.OVERDUE, principalAmount = 62_000.0, interestRate = 4.7))
        assertEquals(6.5, result.interestRate, DELTA)
        assertEquals(LoanStatus.DEFAULT, result.status)
    }
}

class BusinessLoanStrategyTest {
    private val strategy = BusinessLoanStrategy()

    @Test fun `active not due adds 0_5`() {
        val result = strategy.process(loan(type = LoanType.BUSINESS, status = LoanStatus.ACTIVE, dueInDays = 300, interestRate = 4.6))
        assertEquals(5.1, result.interestRate, DELTA)
    }

    @Test fun `overdue over threshold defaults`() {
        val result = strategy.process(loan(type = LoanType.BUSINESS, status = LoanStatus.OVERDUE, principalAmount = 130_000.0, interestRate = 4.0))
        assertEquals(6.0, result.interestRate, DELTA)
        assertEquals(LoanStatus.DEFAULT, result.status)
    }
}
