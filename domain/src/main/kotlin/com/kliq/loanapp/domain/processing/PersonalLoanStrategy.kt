package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import javax.inject.Inject

class PersonalLoanStrategy @Inject constructor() : LoanProcessingStrategy {
    override val type = LoanType.PERSONAL

    override fun process(loan: Loan): Loan = when (loan.status) {
        LoanStatus.ACTIVE ->
            if (loan.dueInDays > 0) {
                loan.addRate(0.3)
            } else if (loan.principalAmount > 10_000) {
                loan.addRate(1.2).withStatus(LoanStatus.OVERDUE)
            } else {
                loan.addRate(0.6)
            }

        LoanStatus.OVERDUE -> loan.addRate(1.5).let {
            if (it.principalAmount > 20_000) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }
}
