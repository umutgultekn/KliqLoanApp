package com.kliq.loanapp.core.model

/**
 * The five portfolio segments. Replaces the starter's untyped segment indices.
 * [ALL] matches everything; the others match a single [LoanStatus].
 */
enum class PortfolioFilter(val matchedStatus: LoanStatus?) {
    ALL(null),
    ACTIVE(LoanStatus.ACTIVE),
    OVERDUE(LoanStatus.OVERDUE),
    DEFAULT(LoanStatus.DEFAULT),
    PAID(LoanStatus.PAID);

    fun matches(loan: Loan): Boolean = matchedStatus == null || loan.status == matchedStatus
}
