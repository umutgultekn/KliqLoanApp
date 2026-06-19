package com.kliq.loanapp.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** Drives a confirmation dialog from a single config. */
@Immutable
data class DialogConfig(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val dismissLabel: String,
)

@Composable
fun KliqDialog(config: DialogConfig, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { TitleText(config.title) },
        text = { BodyText(config.message) },
        confirmButton = { KliqTextButton(text = config.confirmLabel, onClick = onConfirm) },
        dismissButton = { KliqTextButton(text = config.dismissLabel, onClick = onDismiss) },
        containerColor = KliqTheme.colors.surface,
        shape = KliqTheme.shapes.cardLarge,
    )
}

/** Specialized convenience wrapper that builds the [DialogConfig] inline. */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) = KliqDialog(DialogConfig(title, message, confirmLabel, dismissLabel), onConfirm, onDismiss)
