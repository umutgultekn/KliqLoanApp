package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The single source of truth for spacing. Screens and components use [KliqTheme.spacing] instead of
 * raw `.dp` gaps, so the app's rhythm is tunable in one place.
 */
@Immutable
data class KliqSpacing(
    val xs: Dp = 2.dp,
    val sm: Dp = 4.dp,
    val md: Dp = 8.dp,
    val lg: Dp = 12.dp,
    val xl: Dp = 16.dp,
    val xxl: Dp = 20.dp,
    val xxxl: Dp = 24.dp,
    val huge: Dp = 32.dp,
)

val KliqDefaultSpacing = KliqSpacing()
