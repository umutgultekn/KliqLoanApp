package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanType

/**
 * Per-loan-type processing: interest adjustment + type-specific status escalation. Reads the
 * PRE-decrement `dueInDays` (global due-date progression runs afterwards in [LoanProcessor]); terminal
 * statuses are skipped. Adding a [LoanType] = one new impl + one `@IntoMap` binding (Open/Closed).
 */
interface LoanProcessingStrategy {
    val type: LoanType
    fun process(loan: Loan): Loan
}
