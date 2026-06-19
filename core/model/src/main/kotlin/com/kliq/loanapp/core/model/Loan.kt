package com.kliq.loanapp.core.model

/** The four loan categories present in `loans.json`. */
enum class LoanType { PERSONAL, MORTGAGE, AUTO, BUSINESS }

/** Lifecycle status of a loan. */
enum class LoanStatus {
    ACTIVE,
    OVERDUE,
    DEFAULT,
    PAID;

    /** Terminal statuses receive no type-specific processing (mirrors the starter's missing branch). */
    val isTerminal: Boolean get() = this == DEFAULT || this == PAID
}

/**
 * Immutable domain model for a loan. Field names are idiomatic camelCase; the snake_case JSON
 * representation is handled by a DTO + mapper in the data layer.
 */
data class Loan(
    val name: String,
    val principalAmount: Money,
    val interestRate: Rate,
    val status: LoanStatus,
    val dueInDays: Int,
    val type: LoanType,
) {
    /** Returns a copy with [delta] added to the interest rate. */
    fun addRate(delta: Rate): Loan = copy(interestRate = interestRate + delta)

    /** Returns a copy with the given [newStatus]. */
    fun withStatus(newStatus: LoanStatus): Loan = copy(status = newStatus)
}
