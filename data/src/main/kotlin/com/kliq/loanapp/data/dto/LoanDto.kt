package com.kliq.loanapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire model for `loans.json`. Nullable fields keep parsing resilient to malformed records (the mapper
 * applies the keep/drop policy); kotlinx.serialization is reflection-free, so R8 needs no keep rules.
 */
@Serializable
data class LoanDto(
    @SerialName("name") val name: String? = null,
    @SerialName("principal_amount") val principalAmount: Double? = null,
    @SerialName("interest_rate") val interestRate: Double? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("due_in") val dueInDays: Int? = null,
    @SerialName("type") val type: String? = null,
)
