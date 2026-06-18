package com.kliq.loanapp.core.testing

import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.domain.repository.SessionRepository
import com.kliq.loanapp.domain.service.LoanService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Configurable fake data source: returns [loans], or throws [error] if set. */
class FakeLoanService(
    private val loans: List<Loan> = emptyList(),
    private val error: Throwable? = null,
) : LoanService {
    var persistedCount: Int = 0
        private set

    override suspend fun fetchLoans(): List<Loan> {
        error?.let { throw it }
        return loans
    }

    override suspend fun persistLoans(loans: List<Loan>) {
        persistedCount = loans.size
    }
}

/** In-memory session for asserting login/logout transitions. */
class FakeSessionRepository(initial: Boolean = false) : SessionRepository {
    private val state = MutableStateFlow(initial)
    override val isLoggedIn: Flow<Boolean> = state.asStateFlow()
    override suspend fun setLoggedIn(value: Boolean) { state.value = value }
    val current: Boolean get() = state.value
}
