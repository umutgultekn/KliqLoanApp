package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Authenticates with the given credentials. The single entry point the presentation layer uses for
 * sign-in — ViewModels depend on this use case, not on [AuthRepository] directly, so the auth flow
 * stays a domain concern and the dependency direction (presentation → domain) is preserved.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authRepository.login(email, password)
}
