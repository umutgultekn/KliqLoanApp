package com.kliq.loanapp.core.testing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType

/** Test fixtures, including a few real records from `loans.json` used in golden tests. */
object LoanFixtures {

    fun loan(
        name: String = "Test Loan",
        principalAmount: Double = 10_000.0,
        interestRate: Double = 3.0,
        status: LoanStatus = LoanStatus.ACTIVE,
        dueInDays: Int = 30,
        type: LoanType = LoanType.PERSONAL,
    ): Loan = Loan(name, principalAmount, interestRate, status, dueInDays, type)

    // Real records from loans.json (golden-test anchors).
    val consumerCredit = Loan("Consumer Credit", 8_500.0, 2.9, LoanStatus.ACTIVE, 45, LoanType.PERSONAL)
    val vehicleFinance = Loan("Vehicle Finance", 42_000.0, 3.6, LoanStatus.OVERDUE, -8, LoanType.AUTO)
    val commercialCredit = Loan("Commercial Credit", 95_000.0, 4.3, LoanStatus.DEFAULT, -40, LoanType.BUSINESS)
    val premiumAutoLease = Loan("Premium Auto Lease", 62_000.0, 4.7, LoanStatus.OVERDUE, -14, LoanType.AUTO)
    val debtRestructuring = Loan("Debt Restructuring", 55_000.0, 4.1, LoanStatus.OVERDUE, -12, LoanType.PERSONAL)
}
