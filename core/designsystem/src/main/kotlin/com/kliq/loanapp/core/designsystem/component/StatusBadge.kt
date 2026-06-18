package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** Color is NOT the only status channel: the label text is always shown and a contentDescription is set. */
@Immutable
data class BadgeConfig(
    val label: String,
    val tone: Tone,
    val contentDescription: String,
)

@Composable
fun StatusBadge(config: BadgeConfig, modifier: Modifier = Modifier) {
    KliqText(
        text = config.label,
        style = KliqTextStyle.Badge,
        color = KliqTheme.colors.onPrimary,
        modifier = modifier
            .clip(KliqTheme.shapes.badge)
            .background(KliqTheme.colors.colorFor(config.tone))
            .padding(horizontal = KliqTheme.spacing.md, vertical = KliqTheme.spacing.xs)
            .semantics { contentDescription = config.contentDescription },
    )
}
