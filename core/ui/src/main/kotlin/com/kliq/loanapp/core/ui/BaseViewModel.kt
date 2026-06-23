package com.kliq.loanapp.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.ui.error.asUiText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base for screen ViewModels: shared loading/error plumbing — an immutable [uiState], an [isLoading]
 * signal, a one-shot [events] channel, and a [launchSafe] that toggles loading and routes errors.
 */
abstract class BaseViewModel<S>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    // Reference-counted so overlapping loading launches don't clear the flag early. isLoading is a
    // pure derivation of this single source of truth, so the flag can never drift out of sync with
    // the count (a second writable flow could race on interleaved finishes).
    private val activeLoads = MutableStateFlow(0)

    /** Shared request/action loading signal — true while any `launchSafe(loading = true)` is in flight. */
    val isLoading: StateFlow<Boolean> =
        activeLoads.map { it > 0 }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    protected val currentState: S get() = _uiState.value

    protected fun setState(reducer: S.() -> S) = _uiState.update(reducer)

    // trySend keeps emission synchronous and call-ordered without spawning a coroutine.
    protected fun sendEvent(event: UiEvent) {
        _events.trySend(event)
    }

    /**
     * Runs [block] in [viewModelScope]; while [loading] is true the (reference-counted) [isLoading]
     * flag is held. Rethrows cancellation; routes other failures to [onError] (a snackbar by default).
     */
    // Intentional error boundary: catch-all maps to AppError; CancellationException is rethrown above.
    @Suppress("TooGenericExceptionCaught")
    protected fun launchSafe(
        loading: Boolean = false,
        onError: (AppError) -> Unit = { sendEvent(UiEvent.ShowSnackbar(it.asUiText())) },
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch {
        if (loading) activeLoads.update { it + 1 }
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            onError(throwable.toAppError())
        } finally {
            if (loading) activeLoads.update { (it - 1).coerceAtLeast(0) }
        }
    }
}
