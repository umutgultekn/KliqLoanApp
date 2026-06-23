package com.kliq.loanapp.data.repository

import com.kliq.loanapp.data.service.AuthService
import com.kliq.loanapp.domain.repository.AuthRepository
import com.kliq.loanapp.domain.repository.SessionRepository
import javax.inject.Inject

/** Orchestrates the [AuthService] backend with the session: a successful sign-in flips the session flag. */
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val sessionRepository: SessionRepository,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> =
        authService.login(email, password).onSuccess { sessionRepository.setLoggedIn(true) }

    override suspend fun logout() {
        authService.logout()
        sessionRepository.setLoggedIn(false)
    }
}
