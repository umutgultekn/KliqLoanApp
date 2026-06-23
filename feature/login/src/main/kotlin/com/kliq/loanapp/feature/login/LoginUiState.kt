package com.kliq.loanapp.feature.login

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.designsystem.component.FieldUiState

@Immutable
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailState: FieldUiState = FieldUiState.Idle,
    val passwordState: FieldUiState = FieldUiState.Idle,
    val submitEnabled: Boolean = false,
)
