package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Centralized elevation tokens. */
@Immutable
data class KliqElevation(
    val none: Dp = 0.dp,
    val card: Dp = 1.dp,
    val raised: Dp = 4.dp,
    val overlay: Dp = 8.dp,
)

val KliqDefaultElevation = KliqElevation()
