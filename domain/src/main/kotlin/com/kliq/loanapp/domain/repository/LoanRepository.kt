package com.kliq.loanapp.domain.repository

import com.kliq.loanapp.core.model.Loan
import kotlinx.coroutines.flow.Flow

/**
 * Raw (unprocessed) domain loans as a reactive [Flow] — the production data-layer shape; failures
 * surface as Flow exceptions, mapped to the AppError taxonomy in the implementation.
 */
interface LoanRepository {
    fun getLoans(): Flow<List<Loan>>
}
