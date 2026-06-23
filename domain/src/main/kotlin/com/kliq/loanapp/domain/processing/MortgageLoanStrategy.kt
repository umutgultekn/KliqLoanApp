package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.model.Rate
import javax.inject.Inject

class MortgageLoanStrategy @Inject constructor() : LoanProcessingStrategy {
    override val type = LoanType.MORTGAGE

    override fun process(loan: Loan): Loan = when (loan.status) {
        LoanStatus.ACTIVE ->
            if (loan.dueInDays > 0) {
                loan.addRate(ON_TIME_BUMP)
            } else {
                loan.addRate(LATE_BUMP).withStatus(LoanStatus.OVERDUE)
            }

        LoanStatus.OVERDUE -> loan.addRate(OVERDUE_BUMP).let {
            // Mortgage escalates to default based on the PRE-decrement due window.
            if (loan.dueInDays < ESCALATE_TO_DEFAULT_BELOW_DAYS) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }

    private companion object {
        val ON_TIME_BUMP = Rate(0.1)
        val LATE_BUMP = Rate(0.4)
        val OVERDUE_BUMP = Rate(0.8)
        const val ESCALATE_TO_DEFAULT_BELOW_DAYS = -60
    }
}
