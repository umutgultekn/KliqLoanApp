package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.common.ui.Tone
import com.kliq.loanapp.core.designsystem.text.asString
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/**
 * Color is NOT the only status channel: the label text is always shown and a contentDescription is
 * set. Both are [UiText] so the label and TalkBack announcement localize through the app's resource
 * seam instead of carrying hardcoded strings.
 */
@Immutable
data class BadgeConfig(
    val label: UiText,
    val tone: Tone,
    val contentDescription: UiText,
)

@Composable
fun StatusBadge(config: BadgeConfig, modifier: Modifier = Modifier) {
    val description = config.contentDescription.asString()
    KliqText(
        text = config.label.asString(),
        style = KliqTextStyle.Badge,
        color = KliqTheme.colors.onColorFor(config.tone),
        modifier = modifier
            .clip(KliqTheme.shapes.badge)
            .background(KliqTheme.colors.colorFor(config.tone))
            .padding(horizontal = KliqTheme.spacing.md, vertical = KliqTheme.spacing.xs)
            .semantics { contentDescription = description },
    )
}
