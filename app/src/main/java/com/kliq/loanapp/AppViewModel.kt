package com.kliq.loanapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.domain.usecase.ObserveAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

/**
 * Resolves the auth-gated START destination from the session and exposes the shared [Navigator].
 *
 * [startState] is intentionally a one-shot startup resolver: it reads the session ONCE (`take(1)`)
 * to pick Login vs Home for the initial graph, then never moves the user again. All runtime
 * navigation (login success, logout) flows through the [Navigator] — the single runtime authority —
 * so there are not two competing sources of truth. A future session-expiry path would emit a
 * Navigator command rather than rely on this gate.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
    val navigator: Navigator,
) : ViewModel() {

    val startState: StateFlow<StartState> = observeAuthState()
        .map<Boolean, StartState> { StartState.Ready(loggedIn = it) }
        .take(1)
        .stateIn(viewModelScope, SharingStarted.Eagerly, StartState.Loading)
}

sealed interface StartState {
    data object Loading : StartState
    data class Ready(val loggedIn: Boolean) : StartState
}
