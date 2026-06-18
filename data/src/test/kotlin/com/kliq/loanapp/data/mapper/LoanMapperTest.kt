package com.kliq.loanapp.data.mapper

import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.data.dto.LoanDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LoanMapperTest {

    @Test fun `maps a well-formed dto`() {
        val dto = LoanDto("Consumer Credit", 8_500.0, 2.9, "active", 45, "personal")
        val loan = dto.toDomainOrNull()!!
        assertEquals("Consumer Credit", loan.name)
        assertEquals(8_500.0, loan.principalAmount, 0.0)
        assertEquals(LoanStatus.ACTIVE, loan.status)
        assertEquals(LoanType.PERSONAL, loan.type)
    }

    @Test fun `unknown type is dropped`() {
        val dto = LoanDto("X", 1.0, 1.0, "active", 1, "crypto")
        assertNull(dto.toDomainOrNull())
    }

    @Test fun `unknown status defaults to active`() {
        val dto = LoanDto("X", 1.0, 1.0, "frozen", 1, "auto")
        assertEquals(LoanStatus.ACTIVE, dto.toDomainOrNull()!!.status)
    }

    @Test fun `missing required numeric field is dropped`() {
        val dto = LoanDto(name = "X", principalAmount = null, interestRate = 1.0, status = "active", dueInDays = 1, type = "auto")
        assertNull(dto.toDomainOrNull())
    }

    @Test fun `status parsing is case and whitespace tolerant`() {
        val dto = LoanDto("X", 1.0, 1.0, " OverDue ", 1, " Business ")
        val loan = dto.toDomainOrNull()!!
        assertEquals(LoanStatus.OVERDUE, loan.status)
        assertEquals(LoanType.BUSINESS, loan.type)
    }
}
