package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** Surface preset bound to Kliq card shape/elevation/padding — the standard container for content. */
@Composable
fun KliqCard(
    modifier: Modifier = Modifier,
    color: Color = KliqTheme.colors.surface,
    shape: Shape = KliqTheme.shapes.card,
    elevation: Dp = KliqTheme.elevation.card,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it },
        shape = shape,
        color = color,
        shadowElevation = elevation,
    ) {
        Column(modifier = Modifier.padding(KliqTheme.spacing.xl), content = content)
    }
}

/** Text button in the Kliq palette (Material's default would be purple). */
@Composable
fun KliqTextButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(text = text, style = KliqTheme.typography.label, color = KliqTheme.colors.primary)
    }
}

/** Themed icon button with an optional [stateDescription] for accessible toggles. */
@Composable
fun KliqIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    stateDescription: String? = null,
) {
    IconButton(
        onClick = onClick,
        modifier = if (stateDescription != null) {
            modifier.semantics { this.stateDescription = stateDescription }
        } else {
            modifier
        },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (tint != Color.Unspecified) tint else KliqTheme.colors.textSecondary,
        )
    }
}

/** Config for a chip — config-driven like the other Kliq components. */
@Immutable
data class ChipConfig(
    val label: String,
    val selected: Boolean,
    val enabled: Boolean = true,
)

/** Material3 filter chip themed from Kliq tokens (ripple, selected check, a11y role for free). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KliqFilterChip(config: ChipConfig, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = config.selected,
        onClick = onClick,
        enabled = config.enabled,
        label = { KliqText(text = config.label, style = KliqTextStyle.Caption) },
        modifier = modifier,
        shape = KliqTheme.shapes.pill,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = KliqTheme.colors.surface,
            labelColor = KliqTheme.colors.textPrimary,
            selectedContainerColor = KliqTheme.colors.primary,
            selectedLabelColor = KliqTheme.colors.onPrimary,
        ),
    )
}
