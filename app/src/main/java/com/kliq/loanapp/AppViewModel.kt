package com.kliq.loanapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Resolves the auth-gated start destination from the session and exposes the shared [Navigator]. */
@HiltViewModel
class AppViewModel @Inject constructor(
    sessionRepository: SessionRepository,
    val navigator: Navigator,
) : ViewModel() {

    val startState: StateFlow<StartState> = sessionRepository.isLoggedIn
        .map<Boolean, StartState> { StartState.Ready(loggedIn = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, StartState.Loading)
}

sealed interface StartState {
    data object Loading : StartState
    data class Ready(val loggedIn: Boolean) : StartState
}
