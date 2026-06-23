package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.model.Money
import com.kliq.loanapp.core.model.Rate
import javax.inject.Inject

class PersonalLoanStrategy @Inject constructor() : LoanProcessingStrategy {
    override val type = LoanType.PERSONAL

    override fun process(loan: Loan): Loan = when (loan.status) {
        LoanStatus.ACTIVE ->
            if (loan.dueInDays > 0) {
                loan.addRate(ON_TIME_BUMP)
            } else if (loan.principalAmount > ESCALATE_TO_OVERDUE_OVER) {
                loan.addRate(LATE_LARGE_BUMP).withStatus(LoanStatus.OVERDUE)
            } else {
                loan.addRate(LATE_SMALL_BUMP)
            }

        LoanStatus.OVERDUE -> loan.addRate(OVERDUE_BUMP).let {
            if (it.principalAmount > ESCALATE_TO_DEFAULT_OVER) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }

    // Per-cycle interest bumps and the principal thresholds that escalate status — the personal-loan
    // business rules, named so the policy reads declaratively instead of as bare literals.
    private companion object {
        val ON_TIME_BUMP = Rate(0.3)
        val LATE_SMALL_BUMP = Rate(0.6)
        val LATE_LARGE_BUMP = Rate(1.2)
        val OVERDUE_BUMP = Rate(1.5)
        val ESCALATE_TO_OVERDUE_OVER = Money(10_000.0)
        val ESCALATE_TO_DEFAULT_OVER = Money(20_000.0)
    }
}
