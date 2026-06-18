package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
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

/** Material3 filter chip themed from Kliq tokens (ripple, selected check, a11y role for free). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KliqFilterChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, style = KliqTheme.typography.caption) },
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
