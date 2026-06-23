package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.model.Money
import com.kliq.loanapp.core.model.Rate
import javax.inject.Inject

class AutoLoanStrategy @Inject constructor() : LoanProcessingStrategy {
    override val type = LoanType.AUTO

    override fun process(loan: Loan): Loan = when (loan.status) {
        LoanStatus.ACTIVE ->
            if (loan.dueInDays > 0) {
                loan.addRate(ON_TIME_BUMP)
            } else {
                loan.addRate(LATE_BUMP).withStatus(LoanStatus.OVERDUE)
            }

        LoanStatus.OVERDUE -> loan.addRate(OVERDUE_BUMP).let {
            if (it.principalAmount > ESCALATE_TO_DEFAULT_OVER) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }

    private companion object {
        val ON_TIME_BUMP = Rate(0.4)
        val LATE_BUMP = Rate(0.9)
        val OVERDUE_BUMP = Rate(1.8)
        val ESCALATE_TO_DEFAULT_OVER = Money(50_000.0)
    }
}
