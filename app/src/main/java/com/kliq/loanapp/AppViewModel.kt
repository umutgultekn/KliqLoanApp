package com.kliq.loanapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.domain.usecase.ObserveAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

/**
 * Reactive auth gate: the session is the single source of truth and navigation is derived from it,
 * so screens never navigate on login/logout. The first value resolves the start destination
 * ([startState]); every later change routes through the [Navigator].
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
    navigator: Navigator,
) : ViewModel() {

    private val authState: Flow<Boolean> = observeAuthState()

    val startState: StateFlow<StartState> = authState
        .map<Boolean, StartState> { StartState.Ready(loggedIn = it) }
        .take(1)
        .stateIn(viewModelScope, SharingStarted.Eagerly, StartState.Loading)

    init {
        authState
            // The first value is the start destination; route only on subsequent session changes.
            .drop(1)
            .onEach { loggedIn -> navigator.navigate(if (loggedIn) NavCommand.ToHome else NavCommand.ToLogin) }
            .launchIn(viewModelScope)
    }
}

sealed interface StartState {
    data object Loading : StartState
    data class Ready(val loggedIn: Boolean) : StartState
}
