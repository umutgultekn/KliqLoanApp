package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kliq.loanapp.core.designsystem.theme.KliqSizes
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

enum class ButtonStyle { Primary, Secondary, Destructive }

/** Semantic size; the concrete height comes from [KliqSizes] (resolved in [KliqButton]). */
enum class ButtonSize { Small, Medium, Large }

private fun ButtonSize.height(sizes: KliqSizes): Dp = when (this) {
    ButtonSize.Small -> sizes.buttonSmall
    ButtonSize.Medium -> sizes.buttonMedium
    ButtonSize.Large -> sizes.buttonLarge
}

/**
 * Config-driven button: a single immutable [ButtonConfig] drives it, and the component owns its own
 * sizing (touch target, width) so call sites don't patch it with layout modifiers.
 */
@Immutable
data class ButtonConfig(
    val text: String,
    val style: ButtonStyle = ButtonStyle.Primary,
    val size: ButtonSize = ButtonSize.Medium,
    val fullWidth: Boolean = false,
    val enabled: Boolean = true,
    val loading: Boolean = false,
)

@Composable
fun KliqButton(
    config: ButtonConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = KliqTheme.colors
    val sizes = KliqTheme.sizes
    val isSecondary = config.style == ButtonStyle.Secondary
    val container = when (config.style) {
        ButtonStyle.Primary -> colors.primary
        ButtonStyle.Secondary -> colors.surface
        ButtonStyle.Destructive -> colors.statusDefault
    }
    val contentColor = if (isSecondary) colors.primary else colors.onPrimary

    Button(
        onClick = onClick,
        enabled = config.enabled && !config.loading,
        modifier = modifier
            .then(if (config.fullWidth) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = config.size.height(sizes)),
        shape = KliqTheme.shapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = contentColor,
            // Muted, brand-consistent disabled state (not Material's flat grey).
            disabledContainerColor = if (isSecondary) colors.surface else container.copy(alpha = 0.4f),
            disabledContentColor = contentColor.copy(alpha = 0.6f),
        ),
        // Subtle lift on filled styles; the bordered Secondary stays flat.
        elevation = if (isSecondary) {
            null
        } else {
            ButtonDefaults.buttonElevation(
                defaultElevation = KliqTheme.elevation.card,
                pressedElevation = KliqTheme.elevation.none,
            )
        },
        // The Secondary style is surface-on-surface, so give it a visible border.
        border = if (isSecondary) BorderStroke(sizes.borderWidth, colors.border) else null,
    ) {
        if (config.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(sizes.progressIndicator),
                strokeWidth = sizes.strokeWidth,
                color = contentColor,
            )
        } else {
            Text(text = config.text, style = KliqTheme.typography.label)
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    fullWidth: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
) = KliqButton(ButtonConfig(text, ButtonStyle.Primary, size, fullWidth, enabled, loading), onClick, modifier)

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    fullWidth: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
) = KliqButton(ButtonConfig(text, ButtonStyle.Secondary, size, fullWidth, enabled, loading), onClick, modifier)

@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.Medium,
    fullWidth: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false,
) = KliqButton(ButtonConfig(text, ButtonStyle.Destructive, size, fullWidth, enabled, loading), onClick, modifier)

@Preview
@Composable
private fun KliqButtonPreview() {
    KliqTheme {
        KliqButton(config = ButtonConfig(text = "Sign In"), onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
