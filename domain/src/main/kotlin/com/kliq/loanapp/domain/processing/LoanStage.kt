package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.Money

/** A single, pure, dependency-free step in the global loan lifecycle pipeline. */
fun interface LoanStage {
    operator fun invoke(loan: Loan): Loan
}

/** Always advances the due window by one cycle. Runs after the per-type strategy. */
object DueDateProgressionStage : LoanStage {
    override fun invoke(loan: Loan): Loan = loan.copy(dueInDays = loan.dueInDays - 1)
}

/** Global escalation: long-overdue, non-paid loans default. Evaluates the post-decrement window. */
object OverdueDefaultStage : LoanStage {
    override fun invoke(loan: Loan): Loan =
        if (loan.dueInDays < -90 && loan.status != LoanStatus.PAID) {
            loan.withStatus(LoanStatus.DEFAULT)
        } else {
            loan
        }
}

/** Settlement wins last: a fully-repaid loan is marked paid. */
object SettlementStage : LoanStage {
    override fun invoke(loan: Loan): Loan =
        if (loan.principalAmount <= Money.Zero) loan.withStatus(LoanStatus.PAID) else loan
}
