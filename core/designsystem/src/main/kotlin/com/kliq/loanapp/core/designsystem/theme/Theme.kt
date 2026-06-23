package com.kliq.loanapp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalKliqColors = staticCompositionLocalOf<KliqColorScheme> {
    error("No KliqColorScheme provided. Wrap content in KliqTheme { }.")
}
private val LocalKliqTypography = staticCompositionLocalOf<KliqTypography> {
    error("No KliqTypography provided. Wrap content in KliqTheme { }.")
}
private val LocalKliqSpacing = staticCompositionLocalOf<KliqSpacing> {
    error("No KliqSpacing provided. Wrap content in KliqTheme { }.")
}
private val LocalKliqShapes = staticCompositionLocalOf<KliqShapes> {
    error("No KliqShapes provided. Wrap content in KliqTheme { }.")
}
private val LocalKliqElevation = staticCompositionLocalOf<KliqElevation> {
    error("No KliqElevation provided. Wrap content in KliqTheme { }.")
}
private val LocalKliqSizes = staticCompositionLocalOf<KliqSizes> {
    error("No KliqSizes provided. Wrap content in KliqTheme { }.")
}

/**
 * The single source of truth for design tokens, also projected onto Material3 roles so raw Material3
 * components render in the Kliq palette. Read `KliqTheme.*`, never `MaterialTheme.*` directly.
 */
object KliqTheme {
    val colors: KliqColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalKliqColors.current
    val typography: KliqTypography
        @Composable @ReadOnlyComposable
        get() = LocalKliqTypography.current
    val spacing: KliqSpacing
        @Composable @ReadOnlyComposable
        get() = LocalKliqSpacing.current
    val shapes: KliqShapes
        @Composable @ReadOnlyComposable
        get() = LocalKliqShapes.current
    val elevation: KliqElevation
        @Composable @ReadOnlyComposable
        get() = LocalKliqElevation.current
    val sizes: KliqSizes
        @Composable @ReadOnlyComposable
        get() = LocalKliqSizes.current
}

@Composable
fun KliqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: KliqColorScheme = if (darkTheme) KliqDarkColors else KliqLightColors,
    typography: KliqTypography = KliqDefaultTypography,
    spacing: KliqSpacing = KliqDefaultSpacing,
    shapes: KliqShapes = KliqDefaultShapes,
    elevation: KliqElevation = KliqDefaultElevation,
    sizes: KliqSizes = KliqDefaultSizes,
    content: @Composable () -> Unit,
) {
    // Cache the Material projections so they're rebuilt only when a token set actually changes
    // (e.g. light↔dark), not on every recomposition of this provider.
    val materialColors = remember(colors, darkTheme) { colors.toMaterialColorScheme(darkTheme) }
    val materialTypography = remember(typography) { typography.toMaterialTypography() }
    val materialShapes = remember(shapes) {
        Shapes(
            extraSmall = shapes.badge,
            small = shapes.card,
            medium = shapes.card,
            large = shapes.cardLarge,
        )
    }
    CompositionLocalProvider(
        LocalKliqColors provides colors,
        LocalKliqTypography provides typography,
        LocalKliqSpacing provides spacing,
        LocalKliqShapes provides shapes,
        LocalKliqElevation provides elevation,
        LocalKliqSizes provides sizes,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = materialTypography,
            shapes = materialShapes,
            content = content,
        )
    }
}

private fun KliqColorScheme.toMaterialColorScheme(dark: Boolean) = if (dark) {
    darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        background = background,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        onSurfaceVariant = textSecondary,
        outline = border,
        error = statusDefault,
        onError = onPrimary,
    )
} else {
    lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        background = background,
        onBackground = textPrimary,
        surface = surface,
        onSurface = textPrimary,
        onSurfaceVariant = textSecondary,
        outline = border,
        error = statusDefault,
        onError = onPrimary,
    )
}

private fun KliqTypography.toMaterialTypography() = Typography(
    headlineLarge = heading,
    titleMedium = title,
    bodyLarge = body,
    bodyMedium = body,
    bodySmall = caption,
    labelLarge = label,
    labelSmall = badge,
)
