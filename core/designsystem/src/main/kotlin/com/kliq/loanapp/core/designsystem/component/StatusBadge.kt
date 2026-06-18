package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
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
    Text(
        text = config.label,
        style = KliqTheme.typography.badge,
        color = KliqTheme.colors.onPrimary,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(KliqTheme.colors.colorFor(config.tone))
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .semantics { contentDescription = config.contentDescription },
    )
}
