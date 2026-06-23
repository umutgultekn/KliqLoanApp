package com.kliq.loanapp.feature.login

import app.cash.turbine.test
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.AuthReason
import com.kliq.loanapp.core.designsystem.component.FieldUiState
import com.kliq.loanapp.core.testing.FakeAuthRepository
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.core.ui.UiEvent
import com.kliq.loanapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val session = FakeSessionRepository()

    private fun viewModel(loginResult: Result<Unit> = Result.success(Unit)) =
        LoginViewModel(LoginUseCase(FakeAuthRepository(loginResult, session)))

    @Test
    fun `invalid email blocks submission`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("not-an-email")
        vm.onPasswordChange("123456")
        vm.onSubmit()

        assertTrue(vm.uiState.value.emailState is FieldUiState.Error)
        assertFalse(session.current) // never logged in
    }

    @Test
    fun `valid credentials open the session`() = runTest {
        val vm = viewModel(Result.success(Unit))
        vm.onEmailChange("user@kliq.com")
        vm.onPasswordChange("secret1")
        vm.onSubmit()

        // The screen does not navigate; opening the session is what routes to Home (via the auth gate).
        assertTrue(session.current)
    }

    @Test
    fun `failed login surfaces a snackbar and does not open the session`() = runTest {
        val vm = viewModel(Result.failure(AppError.Auth(AuthReason.INVALID_CREDENTIALS)))
        vm.events.test {
            vm.onEmailChange("user@kliq.com")
            vm.onPasswordChange("secret1")
            vm.onSubmit()
            assertTrue(awaitItem() is UiEvent.ShowSnackbar)
        }
        assertFalse(session.current)
    }

    @Test
    fun `shared isLoading is reset after success and after failure`() = runTest {
        val ok = viewModel(Result.success(Unit))
        ok.onEmailChange("user@kliq.com"); ok.onPasswordChange("secret1"); ok.onSubmit()
        assertFalse(ok.isLoading.value)

        val failing = LoginViewModel(
            LoginUseCase(FakeAuthRepository(Result.failure(AppError.Auth(AuthReason.INVALID_CREDENTIALS)), FakeSessionRepository())),
        )
        failing.onEmailChange("user@kliq.com"); failing.onPasswordChange("secret1"); failing.onSubmit()
        assertFalse(failing.isLoading.value)
    }

    @Test
    fun `email validates when advancing via IME Next`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("not-an-email")
        vm.onEmailImeNext()
        assertTrue(vm.uiState.value.emailState is FieldUiState.Error)
    }

    @Test
    fun `submit stays disabled until both fields pass their rules`() = runTest {
        val vm = viewModel()
        assertFalse(vm.uiState.value.submitEnabled) // empty

        vm.onEmailChange("user@kliq.com")
        assertFalse(vm.uiState.value.submitEnabled) // password still invalid

        vm.onPasswordChange("secret1")
        assertTrue(vm.uiState.value.submitEnabled) // both valid

        vm.onEmailChange("not-an-email")
        assertFalse(vm.uiState.value.submitEnabled) // invalid email re-disables
    }

    @Test
    fun `email shows focused on focus then validates on blur`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("a@b.com")
        vm.onEmailFocusChanged(focused = true)
        assertTrue(vm.uiState.value.emailState is FieldUiState.Focused)
        vm.onEmailFocusChanged(focused = false)
        assertTrue(vm.uiState.value.emailState is FieldUiState.Valid)
    }
}
