package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams whether a session is active. The composition root observes this to resolve the auth-gated
 * start destination, depending on the domain use case instead of reaching into [SessionRepository].
 */
class ObserveAuthStateUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<Boolean> = sessionRepository.isLoggedIn
}
