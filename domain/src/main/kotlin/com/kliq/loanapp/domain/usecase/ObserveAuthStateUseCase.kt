package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Streams whether a session is active; the composition root observes it for the auth-gated start
 * destination, depending on the use case rather than [SessionRepository] directly.
 */
class ObserveAuthStateUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<Boolean> = sessionRepository.isLoggedIn
}
