package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.AuthReason
import com.kliq.loanapp.core.testing.FakeAuthRepository
import com.kliq.loanapp.core.testing.FakeSessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUseCasesTest {

    @Test fun `LoginUseCase succeeds and opens the session`() = runTest {
        val session = FakeSessionRepository()
        val login = LoginUseCase(FakeAuthRepository(Result.success(Unit), session))
        assertTrue(login("user@kliq.com", "secret1").isSuccess)
        assertTrue(session.current)
    }

    @Test fun `LoginUseCase passes a failure through and leaves the session closed`() = runTest {
        val session = FakeSessionRepository()
        val login = LoginUseCase(
            FakeAuthRepository(Result.failure(AppError.Auth(AuthReason.INVALID_CREDENTIALS)), session),
        )
        assertTrue(login("bad", "x").isFailure)
        assertFalse(session.current)
    }

    @Test fun `LogoutUseCase clears the session`() = runTest {
        val session = FakeSessionRepository(initial = true)
        LogoutUseCase(FakeAuthRepository(session = session)).invoke()
        assertFalse(session.current)
    }

    @Test fun `ObserveAuthStateUseCase reflects the session state`() = runTest {
        val session = FakeSessionRepository(initial = true)
        val observe = ObserveAuthStateUseCase(session)
        assertEquals(true, observe().first())
        session.setLoggedIn(false)
        assertEquals(false, observe().first())
    }
}
