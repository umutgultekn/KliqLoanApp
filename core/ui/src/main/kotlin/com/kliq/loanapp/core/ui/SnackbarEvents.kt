package com.kliq.loanapp.core.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.kliq.loanapp.core.designsystem.text.asString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Wires a ViewModel's one-shot [UiEvent] stream to a [SnackbarHostState]: collects lifecycle-aware,
 * resolves [com.kliq.loanapp.core.common.text.UiText] to a string, and shows the snackbar. Removes
 * the identical boilerplate every screen would otherwise repeat.
 */
@Composable
fun rememberSnackbarEvents(events: Flow<UiEvent>): SnackbarHostState {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(events) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> scope.launch {
                snackbarHostState.showSnackbar(
                    message = event.message.asString(context),
                    actionLabel = event.actionLabel?.asString(context),
                )
            }
        }
    }
    return snackbarHostState
}
