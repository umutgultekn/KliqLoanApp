package com.kliq.loanapp.domain.repository

import com.kliq.loanapp.core.model.Loan

/** Provides raw (unprocessed) loans, mapped from the data source. */
interface LoanRepository {
    suspend fun getLoans(): Result<List<Loan>>
}
