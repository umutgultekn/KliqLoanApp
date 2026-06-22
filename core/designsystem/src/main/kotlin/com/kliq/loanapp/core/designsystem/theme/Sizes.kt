package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The single source of truth for component dimensions — button heights, icon/logo sizes, stroke and
 * border widths, loading-skeleton block heights. Like [KliqSpacing] for gaps, this keeps fixed
 * component sizes tunable in one place instead of scattered `.dp` literals across screens.
 */
@Immutable
data class KliqSizes(
    val buttonSmall: Dp = 40.dp,
    val buttonMedium: Dp = 52.dp,
    val buttonLarge: Dp = 56.dp,
    val icon: Dp = 24.dp,
    val progressIndicator: Dp = 20.dp,
    val strokeWidth: Dp = 2.dp,
    val borderWidth: Dp = 1.dp,
    val logoHeight: Dp = 44.dp,
    val skeletonHeader: Dp = 104.dp,
    val skeletonRow: Dp = 92.dp,
    // Minimum comfortable width for a loan card; the adaptive grid fits as many columns as fit.
    val loanCardMinWidth: Dp = 300.dp,
)

val KliqDefaultSizes = KliqSizes()
