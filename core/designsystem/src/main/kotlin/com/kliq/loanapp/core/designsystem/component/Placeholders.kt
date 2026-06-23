package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** Centered empty/zero-state with an optional call to action. */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(KliqTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KliqText(text = title, style = KliqTextStyle.Title)
        Spacer(Modifier.height(KliqTheme.spacing.sm))
        KliqText(
            text = message,
            style = KliqTextStyle.Body,
            color = KliqTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(KliqTheme.spacing.lg))
            KliqButton(config = ButtonConfig(text = actionLabel, style = ButtonStyle.Secondary), onClick = onAction)
        }
    }
}
