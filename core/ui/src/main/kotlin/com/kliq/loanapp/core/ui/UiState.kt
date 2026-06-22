package com.kliq.loanapp.core.ui

import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.common.text.UiText

/**
 * The standard content envelope for a data-loading screen: the three mutually-exclusive phases of
 * fetching-then-showing data, so impossible combinations (loading + error + content at once) are
 * unrepresentable. Screens wrap their loaded payload as `UiState<TheirData>` and switch on it
 * exhaustively. Cross-cutting "chrome" (filters, refresh, dialogs) stays alongside it on the
 * screen's own state, not inside this envelope.
 *
 * Form screens (e.g. Login) are not data-loading and keep their own flat state instead.
 */
@Immutable
sealed interface UiState<out T> {
    /** The initial/full-screen load is in progress. */
    data object Loading : UiState<Nothing>

    /** The load failed; [message] is presentation-ready. */
    data class Error(val message: UiText) : UiState<Nothing>

    /** The load succeeded with [data]. */
    data class Content<out T>(val data: T) : UiState<T>
}
