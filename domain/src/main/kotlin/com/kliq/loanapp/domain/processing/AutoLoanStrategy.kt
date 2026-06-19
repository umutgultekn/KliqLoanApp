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
                loan.addRate(Rate(0.4))
            } else {
                loan.addRate(Rate(0.9)).withStatus(LoanStatus.OVERDUE)
            }

        LoanStatus.OVERDUE -> loan.addRate(Rate(1.8)).let {
            if (it.principalAmount > Money(50_000.0)) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }
}
