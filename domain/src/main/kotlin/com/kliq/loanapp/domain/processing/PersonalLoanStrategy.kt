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
                loan.addRate(Rate(0.3))
            } else if (loan.principalAmount > Money(10_000.0)) {
                loan.addRate(Rate(1.2)).withStatus(LoanStatus.OVERDUE)
            } else {
                loan.addRate(Rate(0.6))
            }

        LoanStatus.OVERDUE -> loan.addRate(Rate(1.5)).let {
            if (it.principalAmount > Money(20_000.0)) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }
}
