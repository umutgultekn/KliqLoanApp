package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalKliqColors = staticCompositionLocalOf<KliqColorScheme> {
    error("No KliqColorScheme provided. Wrap content in KliqTheme { }.")
}
private val LocalKliqTypography = staticCompositionLocalOf<KliqTypography> {
    error("No KliqTypography provided. Wrap content in KliqTheme { }.")
}

/** Entry point to the design-system tokens. */
object KliqTheme {
    val colors: KliqColorScheme
        @Composable @ReadOnlyComposable get() = LocalKliqColors.current
    val typography: KliqTypography
        @Composable @ReadOnlyComposable get() = LocalKliqTypography.current
}

@Composable
fun KliqTheme(
    colors: KliqColorScheme = KliqLightColors,
    typography: KliqTypography = KliqDefaultTypography,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalKliqColors provides colors,
        LocalKliqTypography provides typography,
        content = content,
    )
}
