package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.testing.LoanFixtures.loan
import org.junit.Assert.assertEquals
import org.junit.Test

class LoanStageTest {

    @Test fun `due date progression decrements by one`() {
        assertEquals(44, DueDateProgressionStage(loan(dueInDays = 45)).dueInDays)
        assertEquals(-9, DueDateProgressionStage(loan(dueInDays = -8)).dueInDays)
    }

    @Test fun `overdue default escalates non-paid loans beyond minus 90`() {
        assertEquals(LoanStatus.DEFAULT, OverdueDefaultStage(loan(status = LoanStatus.ACTIVE, dueInDays = -91)).status)
        assertEquals(LoanStatus.DEFAULT, OverdueDefaultStage(loan(status = LoanStatus.OVERDUE, dueInDays = -91)).status)
    }

    @Test fun `overdue default leaves paid loans untouched`() {
        assertEquals(LoanStatus.PAID, OverdueDefaultStage(loan(status = LoanStatus.PAID, dueInDays = -91)).status)
    }

    @Test fun `overdue default respects the minus 90 boundary`() {
        assertEquals(LoanStatus.ACTIVE, OverdueDefaultStage(loan(status = LoanStatus.ACTIVE, dueInDays = -90)).status)
    }

    @Test fun `settlement marks fully repaid loans paid`() {
        assertEquals(LoanStatus.PAID, SettlementStage(loan(principalAmount = 0.0, status = LoanStatus.ACTIVE)).status)
        assertEquals(LoanStatus.PAID, SettlementStage(loan(principalAmount = -5.0, status = LoanStatus.OVERDUE)).status)
        assertEquals(LoanStatus.ACTIVE, SettlementStage(loan(principalAmount = 1.0, status = LoanStatus.ACTIVE)).status)
    }
}
