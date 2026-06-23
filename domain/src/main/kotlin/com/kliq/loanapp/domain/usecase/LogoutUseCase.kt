package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Ends the session via [AuthRepository.logout] (not a raw session write from the ViewModel), so
 * sign-out stays an auth-domain operation.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() = authRepository.logout()
}
