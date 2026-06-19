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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base for screen ViewModels: a single immutable [uiState] stream, a one-shot [events] channel, and
 * a [launchSafe] that routes errors to a snackbar by default. Inheritance keeps each screen's state
 * fully its own while removing the shared plumbing.
 */
abstract class BaseViewModel<S>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events: Flow<UiEvent> = _events.receiveAsFlow()

    protected val currentState: S get() = _uiState.value

    protected fun setState(reducer: S.() -> S) = _uiState.update(reducer)

    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    /**
     * Launches [block] in [viewModelScope], rethrowing cancellation (never swallowed) and routing any
     * other failure to [onError], which defaults to a snackbar event.
     */
    // Intentional error boundary: catch-all maps to AppError; CancellationException is rethrown above.
    @Suppress("TooGenericExceptionCaught")
    protected fun launchSafe(
        onError: (AppError) -> Unit = { sendEvent(UiEvent.ShowSnackbar(it.asUiText())) },
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch {
        try {
            block()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            onError(throwable.toAppError())
        }
    }
}
