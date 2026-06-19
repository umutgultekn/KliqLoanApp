package com.kliq.loanapp.core.testing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.model.Money
import com.kliq.loanapp.core.model.Rate

/** Test fixtures, including a few real records from `loans.json` used in golden tests. */
object LoanFixtures {

    // Plain-Double params keep call sites terse; they are wrapped into the Money/Rate value types here.
    fun loan(
        name: String = "Test Loan",
        principalAmount: Double = 10_000.0,
        interestRate: Double = 3.0,
        status: LoanStatus = LoanStatus.ACTIVE,
        dueInDays: Int = 30,
        type: LoanType = LoanType.PERSONAL,
    ): Loan = Loan(name, Money(principalAmount), Rate(interestRate), status, dueInDays, type)

    // Real records from loans.json (golden-test anchors).
    val consumerCredit = Loan("Consumer Credit", Money(8_500.0), Rate(2.9), LoanStatus.ACTIVE, 45, LoanType.PERSONAL)
    val vehicleFinance = Loan("Vehicle Finance", Money(42_000.0), Rate(3.6), LoanStatus.OVERDUE, -8, LoanType.AUTO)
    val commercialCredit =
        Loan("Commercial Credit", Money(95_000.0), Rate(4.3), LoanStatus.DEFAULT, -40, LoanType.BUSINESS)
    val premiumAutoLease =
        Loan("Premium Auto Lease", Money(62_000.0), Rate(4.7), LoanStatus.OVERDUE, -14, LoanType.AUTO)
    val debtRestructuring =
        Loan("Debt Restructuring", Money(55_000.0), Rate(4.1), LoanStatus.OVERDUE, -12, LoanType.PERSONAL)
}
