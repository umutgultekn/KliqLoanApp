package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/**
 * Kliq-styled snackbar, rendered through [androidx.compose.material3.SnackbarHost]'s `snackbar`
 * slot. It owns only the LOOK — shape, colors and typography come from [KliqTheme] — while the
 * queueing, duration, swipe-to-dismiss and TalkBack announcement stay with Material's
 * `SnackbarHostState`. Replacing the host wholesale would mean re-implementing that battle-tested
 * state machine for no visual gain; styling the slot is the correct seam.
 *
 * The dark [KliqColorScheme.primary] surface mirrors the portfolio summary card, so transient
 * messages read as part of the same brand surface family and pop over light content.
 */
@Composable
fun KliqSnackbar(snackbarData: SnackbarData, modifier: Modifier = Modifier) {
    val colors = KliqTheme.colors
    val actionLabel = snackbarData.visuals.actionLabel

    Snackbar(
        modifier = modifier.padding(KliqTheme.spacing.md),
        action = actionLabel?.let { label ->
            {
                TextButton(onClick = { snackbarData.performAction() }) {
                    KliqText(text = label, style = KliqTextStyle.Label, color = colors.onPrimary)
                }
            }
        },
        shape = KliqTheme.shapes.card,
        containerColor = colors.primary,
        contentColor = colors.onPrimary,
        actionContentColor = colors.onPrimary,
    ) {
        KliqText(
            text = snackbarData.visuals.message,
            style = KliqTextStyle.Body,
            color = colors.onPrimary,
        )
    }
}
