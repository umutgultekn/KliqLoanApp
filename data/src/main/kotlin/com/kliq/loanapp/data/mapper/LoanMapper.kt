package com.kliq.loanapp.data.mapper

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.model.Money
import com.kliq.loanapp.core.model.Rate
import com.kliq.loanapp.data.dto.LoanDto

/**
 * Pure DTO → domain mapping with an explicit unknown-value policy:
 *  - missing required field (name/principal/rate/due) or unknown [LoanType] → record DROPPED (null)
 *  - unknown/missing status → defaulted to [LoanStatus.ACTIVE]
 *
 * Dropping malformed/un-typed records (rather than coercing them) keeps type-based processing and
 * portfolio aggregates honest. Logging of dropped records happens at the service boundary so this
 * function stays pure and unit-testable on the JVM. Returns the mapped [Loan], or null if dropped.
 */
internal fun LoanDto.toDomainOrNull(): Loan? {
    val safeName = name ?: return null
    val principal = principalAmount ?: return null
    val rate = interestRate ?: return null
    val due = dueInDays ?: return null
    val loanType = parseType(type) ?: return null
    return Loan(
        name = safeName,
        principalAmount = Money(principal),
        interestRate = Rate(rate),
        status = parseStatus(status),
        dueInDays = due,
        type = loanType,
    )
}

private fun parseType(raw: String?): LoanType? = when (raw?.trim()?.lowercase()) {
    "personal" -> LoanType.PERSONAL
    "mortgage" -> LoanType.MORTGAGE
    "auto" -> LoanType.AUTO
    "business" -> LoanType.BUSINESS
    else -> null
}

private fun parseStatus(raw: String?): LoanStatus = when (raw?.trim()?.lowercase()) {
    "active" -> LoanStatus.ACTIVE
    "overdue" -> LoanStatus.OVERDUE
    "default" -> LoanStatus.DEFAULT
    "paid" -> LoanStatus.PAID
    else -> LoanStatus.ACTIVE
}
