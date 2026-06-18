package com.kliq.loanapp.data.dto

import com.google.gson.annotations.SerializedName

/**
 * Wire model for `loans.json`. Every field carries an explicit [SerializedName] so R8 obfuscation
 * in release builds cannot break Gson's name-based binding. Fields are nullable to stay resilient
 * to malformed records; the mapper applies the keep/drop policy.
 */
data class LoanDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("principal_amount") val principalAmount: Double? = null,
    @SerializedName("interest_rate") val interestRate: Double? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("due_in") val dueInDays: Int? = null,
    @SerializedName("type") val type: String? = null,
)
