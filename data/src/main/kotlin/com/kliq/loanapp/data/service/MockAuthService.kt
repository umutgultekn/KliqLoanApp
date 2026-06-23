package com.kliq.loanapp.data.service

import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Mock auth backend: the client already validated the form and there's no user store to check, so any
 * sign-in succeeds. A real backend would verify credentials here (and could fail) — which is why
 * [login] returns a [Result].
 */
class MockAuthService @Inject constructor(
    private val dispatchers: DispatcherProvider,
) : AuthService {

    override suspend fun login(email: String, password: String): Result<Unit> =
        withContext(dispatchers.io) { Result.success(Unit) }

    override suspend fun logout() = Unit
}
