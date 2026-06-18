package com.kliq.loanapp.domain.repository

import com.kliq.loanapp.core.model.Loan
import kotlinx.coroutines.flow.Flow

/** Provides raw (unprocessed) loans, mapped from the data source. */
interface LoanRepository {
    suspend fun getLoans(): Result<List<Loan>>
}

/** Authentication operations. Implementations also update the session on success. */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
}

/** Single source of truth for the login session. */
interface SessionRepository {
    val isLoggedIn: Flow<Boolean>
    suspend fun setLoggedIn(value: Boolean)
}
