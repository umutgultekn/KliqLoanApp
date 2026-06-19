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
                loan.addRate(Rate(0.1))
            } else {
                loan.addRate(Rate(0.4)).withStatus(LoanStatus.OVERDUE)
            }

        LoanStatus.OVERDUE -> loan.addRate(Rate(0.8)).let {
            // Mortgage escalates to default based on the PRE-decrement due window.
            if (loan.dueInDays < -60) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }
}
