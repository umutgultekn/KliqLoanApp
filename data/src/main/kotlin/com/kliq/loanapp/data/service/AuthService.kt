package com.kliq.loanapp.data.service

/**
 * The authentication backend boundary. `AuthRepository` orchestrates this with the session; a real
 * implementation would perform a network call here, so [login] can fail. Mocked by [MockAuthService].
 */
interface AuthService {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun logout()
}
