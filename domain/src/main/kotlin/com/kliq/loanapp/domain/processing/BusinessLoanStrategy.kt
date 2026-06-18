package com.kliq.loanapp.domain.processing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import javax.inject.Inject

class BusinessLoanStrategy @Inject constructor() : LoanProcessingStrategy {
    override val type = LoanType.BUSINESS

    override fun process(loan: Loan): Loan = when (loan.status) {
        LoanStatus.ACTIVE ->
            if (loan.dueInDays > 0) {
                loan.addRate(0.5)
            } else {
                loan.addRate(1.0).withStatus(LoanStatus.OVERDUE)
            }

        LoanStatus.OVERDUE -> loan.addRate(2.0).let {
            if (it.principalAmount > 100_000) it.withStatus(LoanStatus.DEFAULT) else it
        }

        else -> loan
    }
}
