package com.kliq.loanapp.core.ui

import com.kliq.loanapp.core.common.text.UiText

/**
 * One-shot UI effects. Navigation is intentionally NOT here — it flows through the single
 * [com.kliq.loanapp.core.common.navigation.Navigator] channel, keeping one source of nav truth.
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: UiText) : UiEvent
}
