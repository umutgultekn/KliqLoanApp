package com.kliq.loanapp.domain.service

import com.kliq.loanapp.core.model.Loan

/**
 * Data-source port for loans. Kept as an interface (as in the starter) so it can be faked in tests;
 * the production implementation reads `loans.json`. Returns domain models — mapping happens in the
 * implementation.
 */
interface LoanService {
    suspend fun fetchLoans(): List<Loan>
}
