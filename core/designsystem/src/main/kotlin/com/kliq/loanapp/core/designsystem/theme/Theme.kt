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

/**
 * The single source of truth for design tokens. [KliqTheme] also PROJECTS its tokens onto Material3
 * roles (color/typography/shapes) so raw Material3 components — Button, OutlinedTextField, Surface,
 * Snackbar, ripples — render in the Kliq palette instead of the default purple baseline.
 *
 * Screens and components read `KliqTheme.colors/typography/spacing/shapes/elevation` — never
 * `MaterialTheme.*` directly.
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
}

@Composable
fun KliqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: KliqColorScheme = if (darkTheme) KliqDarkColors else KliqLightColors,
    typography: KliqTypography = KliqDefaultTypography,
    spacing: KliqSpacing = KliqDefaultSpacing,
    shapes: KliqShapes = KliqDefaultShapes,
    elevation: KliqElevation = KliqDefaultElevation,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalKliqColors provides colors,
        LocalKliqTypography provides typography,
        LocalKliqSpacing provides spacing,
        LocalKliqShapes provides shapes,
        LocalKliqElevation provides elevation,
    ) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(darkTheme),
            typography = typography.toMaterialTypography(),
            shapes = Shapes(
                extraSmall = shapes.badge,
                small = shapes.card,
                medium = shapes.card,
                large = shapes.cardLarge,
            ),
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
