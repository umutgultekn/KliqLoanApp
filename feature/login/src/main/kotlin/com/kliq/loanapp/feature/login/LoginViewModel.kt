package com.kliq.loanapp.feature.login

import androidx.lifecycle.SavedStateHandle
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.validation.EmailRule
import com.kliq.loanapp.core.common.validation.MinLengthRule
import com.kliq.loanapp.core.common.validation.ValidationResult
import com.kliq.loanapp.core.designsystem.component.FieldUiState
import com.kliq.loanapp.core.ui.BaseViewModel
import com.kliq.loanapp.core.ui.UiEvent
import com.kliq.loanapp.core.ui.error.asUiText
import com.kliq.loanapp.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<LoginUiState>(LoginUiState()) {

    private val emailRule = EmailRule(UiText.res(R.string.login_email_error))
    private val passwordRule = MinLengthRule(min = 6, error = UiText.res(R.string.login_password_error))

    init {
        // Restore field values across process death.
        val email = savedStateHandle.get<String>(KEY_EMAIL).orEmpty()
        val password = savedStateHandle.get<String>(KEY_PASSWORD).orEmpty()
        if (email.isNotEmpty() || password.isNotEmpty()) {
            setState { copy(email = email, password = password) }
        }
    }

    fun onEmailChange(value: String) {
        savedStateHandle[KEY_EMAIL] = value
        setState { copy(email = value, emailState = revalidateIfError(emailState, value, ::validateEmail)) }
    }

    fun onPasswordChange(value: String) {
        savedStateHandle[KEY_PASSWORD] = value
        setState {
            copy(
                password = value,
                passwordState = if (passwordState is FieldUiState.Error) FieldUiState.Idle else passwordState,
            )
        }
    }

    fun onEmailFocusChanged(focused: Boolean) {
        setState { copy(emailState = focusState(focused, email, ::validateEmail)) }
    }

    fun onPasswordFocusChanged(focused: Boolean) {
        setState {
            copy(
                passwordState = when {
                    focused -> FieldUiState.Focused
                    passwordState is FieldUiState.Error -> passwordState
                    else -> FieldUiState.Idle
                },
            )
        }
    }

    /** Validate the email when the user advances to the password field via the IME "Next" action. */
    fun onEmailImeNext() {
        setState { copy(emailState = validateEmail(email)) }
    }

    fun onSubmit() {
        val emailState = validateEmail(currentState.email)
        val passwordState = validatePassword(currentState.password)
        setState { copy(emailState = emailState, passwordState = passwordState) }
        if (emailState is FieldUiState.Valid && passwordState is FieldUiState.Valid) {
            login()
        }
    }

    // Loading is the shared BaseViewModel.isLoading flag (toggled by launchSafe); the known auth
    // failure is surfaced as a snackbar, and any unexpected throwable falls through to launchSafe's
    // default snackbar handler.
    private fun login() = launchSafe(loading = true) {
        loginUseCase(currentState.email, currentState.password)
            .onSuccess {
                navigator.navigate(NavCommand.ToHome)
            }
            .onFailure {
                sendEvent(UiEvent.ShowSnackbar(it.toAppError().asUiText()))
            }
    }

    private fun validateEmail(value: String): FieldUiState = emailRule.validate(value).toFieldState()
    private fun validatePassword(value: String): FieldUiState = passwordRule.validate(value).toFieldState()

    private fun ValidationResult.toFieldState(): FieldUiState = when (this) {
        is ValidationResult.Success -> FieldUiState.Valid
        is ValidationResult.Failure -> FieldUiState.Error(message)
    }

    private fun focusState(focused: Boolean, value: String, validate: (String) -> FieldUiState): FieldUiState = when {
        focused -> FieldUiState.Focused
        value.isBlank() -> FieldUiState.Idle
        else -> validate(value)
    }

    private fun revalidateIfError(current: FieldUiState, value: String, validate: (String) -> FieldUiState): FieldUiState =
        if (current is FieldUiState.Error) validate(value) else current

    private companion object {
        const val KEY_EMAIL = "login_email"
        const val KEY_PASSWORD = "login_password"
    }
}
