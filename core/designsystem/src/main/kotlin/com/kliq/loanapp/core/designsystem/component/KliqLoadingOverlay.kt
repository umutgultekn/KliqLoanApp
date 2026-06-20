package com.kliq.loanapp.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.kliq.loanapp.core.designsystem.R
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/**
 * A full-screen, blocking loading overlay — a dimmed [KliqColorScheme.scrim] veil that swallows
 * input, with a centered branded indicator card. The banking/fintech pattern for security-sensitive
 * actions (e.g. sign-in) where the user must wait: it makes the wait explicit and prevents
 * interaction, unlike an inline button spinner. NOT a Material `ProgressDialog` (deprecated) — it is
 * a composed overlay layer placed on top of the screen.
 *
 * Drive [visible] from the shared `BaseViewModel.isLoading`. For initial content loading use a
 * skeleton, and for pull-to-refresh a refresh indicator — an overlay there would hide the content.
 */
@Composable
fun KliqLoadingOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        val description = message ?: stringResource(R.string.kliq_loading)
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(KliqTheme.colors.scrim)
                // Swallow all touches so content behind the overlay can't be interacted with.
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .semantics { contentDescription = description },
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = KliqTheme.shapes.cardLarge,
                color = KliqTheme.colors.surface,
                shadowElevation = KliqTheme.elevation.overlay,
            ) {
                Column(
                    modifier = Modifier.padding(KliqTheme.spacing.huge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(KliqTheme.spacing.lg),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(KliqTheme.sizes.icon),
                        color = KliqTheme.colors.primary,
                        strokeWidth = KliqTheme.sizes.strokeWidth,
                    )
                    if (message != null) {
                        KliqText(text = message, style = KliqTextStyle.Body, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
