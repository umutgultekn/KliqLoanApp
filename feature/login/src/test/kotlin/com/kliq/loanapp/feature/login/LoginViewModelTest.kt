package com.kliq.loanapp.feature.login

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.AuthReason
import com.kliq.loanapp.core.designsystem.component.FieldUiState
import com.kliq.loanapp.core.testing.FakeAuthRepository
import com.kliq.loanapp.core.testing.FakeNavigator
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.core.ui.UiEvent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val session = FakeSessionRepository()
    private val navigator = FakeNavigator()

    private fun viewModel(loginResult: Result<Unit> = Result.success(Unit)) =
        LoginViewModel(FakeAuthRepository(loginResult, session), navigator, SavedStateHandle())

    @Test
    fun `invalid email blocks submission`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("not-an-email")
        vm.onPasswordChange("123456")
        vm.onSubmit()

        assertTrue(vm.uiState.value.emailState is FieldUiState.Error)
        assertFalse(session.current)
        assertTrue(navigator.received.isEmpty())
    }

    @Test
    fun `valid credentials log in and navigate to portfolio`() = runTest {
        val vm = viewModel(Result.success(Unit))
        vm.onEmailChange("user@kliq.com")
        vm.onPasswordChange("secret1")
        vm.onSubmit()

        assertTrue(session.current)
        assertEquals(
            NavCommand.To(KliqRoute.Home, popUpTo = KliqRoute.Login, inclusive = true),
            navigator.last,
        )
    }

    @Test
    fun `failed login surfaces a snackbar and does not navigate`() = runTest {
        val vm = viewModel(Result.failure(AppError.Auth(AuthReason.INVALID_CREDENTIALS)))
        vm.events.test {
            vm.onEmailChange("user@kliq.com")
            vm.onPasswordChange("secret1")
            vm.onSubmit()
            assertTrue(awaitItem() is UiEvent.ShowSnackbar)
        }
        assertTrue(navigator.received.isEmpty())
        assertFalse(session.current)
    }

    @Test
    fun `isSubmitting is reset after success and after failure`() = runTest {
        val ok = viewModel(Result.success(Unit))
        ok.onEmailChange("user@kliq.com"); ok.onPasswordChange("secret1"); ok.onSubmit()
        assertFalse(ok.uiState.value.isSubmitting)

        val failing = LoginViewModel(
            FakeAuthRepository(Result.failure(AppError.Auth(AuthReason.INVALID_CREDENTIALS)), FakeSessionRepository()),
            FakeNavigator(),
            SavedStateHandle(),
        )
        failing.onEmailChange("user@kliq.com"); failing.onPasswordChange("secret1"); failing.onSubmit()
        assertFalse(failing.uiState.value.isSubmitting)
    }

    @Test
    fun `email validates when advancing via IME Next`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("not-an-email")
        vm.onEmailImeNext()
        assertTrue(vm.uiState.value.emailState is FieldUiState.Error)
    }

    @Test
    fun `email shows focused on focus then validates on blur`() = runTest {
        val vm = viewModel()
        vm.onEmailChange("a@b.com")
        vm.onEmailFocusChanged(focused = true)
        assertEquals(FieldUiState.Focused, vm.uiState.value.emailState)
        vm.onEmailFocusChanged(focused = false)
        assertEquals(FieldUiState.Valid, vm.uiState.value.emailState)
    }
}
