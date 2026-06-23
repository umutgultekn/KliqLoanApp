package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Authenticates with the given credentials. ViewModels depend on this use case, not on
 * [AuthRepository] directly, keeping auth a domain concern (presentation → domain).
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        authRepository.login(email, password)
}
