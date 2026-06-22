package com.kliq.loanapp.domain.repository

import com.kliq.loanapp.core.model.Loan
import kotlinx.coroutines.flow.Flow

/**
 * Provides raw (unprocessed) domain loans as a reactive stream. Returning a [Flow] is the production
 * data-layer shape: a real implementation backed by a database/network would re-emit on change; here
 * the single emission comes from the bundled JSON. Failures are surfaced as Flow exceptions, mapped
 * to the [com.kliq.loanapp.core.common.result.AppError] taxonomy in the implementation.
 */
interface LoanRepository {
    fun getLoans(): Flow<List<Loan>>
}
