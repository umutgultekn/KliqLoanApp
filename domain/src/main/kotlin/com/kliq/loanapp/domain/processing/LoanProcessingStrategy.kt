package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanType

/**
 * Per-loan-type processing: interest adjustment + type-specific status escalation.
 *
 * Reads the PRE-decrement `dueInDays` (the global due-date progression runs afterwards in
 * [LoanProcessor]). Only `ACTIVE` and `OVERDUE` loans are processed — terminal statuses are
 * skipped by the processor, mirroring the starter's missing `else` branch.
 *
 * Adding a new [LoanType] requires only a new implementation + one Hilt `@IntoMap` binding —
 * no existing code changes (Open/Closed Principle).
 */
interface LoanProcessingStrategy {
    val type: LoanType
    fun process(loan: Loan): Loan
}
