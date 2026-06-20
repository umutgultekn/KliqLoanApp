package com.kliq.loanapp.feature.login

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.designsystem.component.FieldUiState

@Immutable
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailState: FieldUiState = FieldUiState.Idle,
    val passwordState: FieldUiState = FieldUiState.Idle,
) {
    // In-flight submission is the shared BaseViewModel.isLoading flag; the button disables itself
    // while loading, so submitEnabled only needs the fields to be filled.
    val submitEnabled: Boolean
        get() = email.isNotBlank() && password.isNotBlank()
}
