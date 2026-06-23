package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The single source of truth for fixed component dimensions (button/icon/logo sizes, stroke widths),
 * tunable in one place instead of scattered `.dp` literals — like [KliqSpacing] for gaps.
 */
@Immutable
data class KliqSizes(
    val buttonSmall: Dp = 40.dp,
    val buttonMedium: Dp = 52.dp,
    val buttonLarge: Dp = 56.dp,
    val progressIndicator: Dp = 20.dp,
    val strokeWidth: Dp = 2.dp,
    val borderWidth: Dp = 1.dp,
    val logoHeight: Dp = 44.dp,
)

val KliqDefaultSizes = KliqSizes()
