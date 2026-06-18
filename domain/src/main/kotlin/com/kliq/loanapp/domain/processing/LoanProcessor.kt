package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanType
import javax.inject.Inject

/**
 * Orchestrates loan processing, faithfully preserving the starter's intra-loop order:
 *   1. per-type strategy (reads PRE-decrement dueInDays; interest + type status)
 *   2. due-date progression (dueInDays -= 1)
 *   3. global overdue→default rule
 *   4. settlement (principal <= 0 → paid)
 *
 * Terminal loans (default/paid) skip the per-type strategy — exactly like the starter, which has no
 * branch for them — but are still decremented and still pass the global stages.
 */
class LoanProcessor @Inject constructor(
    private val strategies: Map<LoanType, @JvmSuppressWildcards LoanProcessingStrategy>,
) {
    private val stages: List<LoanStage> =
        listOf(DueDateProgressionStage, OverdueDefaultStage, SettlementStage)

    init {
        // Fail fast if a strategy binding is missing, rather than crashing on a specific loan type.
        val missing = LoanType.entries.toSet() - strategies.keys
        require(missing.isEmpty()) { "No LoanProcessingStrategy bound for: $missing" }
    }

    fun process(loan: Loan): Loan {
        val typed = if (loan.status.isTerminal) loan else strategies.getValue(loan.type).process(loan)
        return stages.fold(typed) { acc, stage -> stage(acc) }
    }

    fun process(loans: List<Loan>): List<Loan> = loans.map(::process)
}
