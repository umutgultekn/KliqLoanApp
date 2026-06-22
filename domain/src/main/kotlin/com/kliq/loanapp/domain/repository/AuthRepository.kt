package com.kliq.loanapp.domain.repository

/** Authentication operations. Implementations also update the session on success. */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
}
