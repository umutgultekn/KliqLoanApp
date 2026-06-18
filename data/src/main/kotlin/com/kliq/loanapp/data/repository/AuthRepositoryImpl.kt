package com.kliq.loanapp.data.repository

import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.AuthReason
import com.kliq.loanapp.domain.repository.AuthRepository
import com.kliq.loanapp.domain.repository.SessionRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Mock authentication: accepts any credentials that pass the (lenient, behavior-preserving) checks
 * and opens a session. The single source of truth for "logged in" is the [SessionRepository].
 */
class AuthRepositoryImpl @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val dispatchers: DispatcherProvider,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> =
        withContext(dispatchers.io) {
            val credentialsValid = email.isNotBlank() && email.contains('@') && password.length >= 6
            if (credentialsValid) {
                sessionRepository.setLoggedIn(true)
                Result.success(Unit)
            } else {
                Result.failure(AppError.Auth(AuthReason.INVALID_CREDENTIALS))
            }
        }

    override suspend fun logout() = sessionRepository.setLoggedIn(false)
}
