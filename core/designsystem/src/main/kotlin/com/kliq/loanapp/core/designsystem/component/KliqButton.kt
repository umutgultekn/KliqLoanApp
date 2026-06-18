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
import androidx.compose.ui.unit.dp
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

enum class ButtonStyle { Primary, Secondary, Destructive }

enum class ButtonSize(val minHeight: Dp) { Small(40.dp), Medium(48.dp), Large(52.dp) }

/**
 * Drives a button from a single immutable data class rather than many individual attributes —
 * the config-driven component pattern. The component owns its own sizing (touch target, width) so
 * call sites never patch it with layout modifiers.
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
    val container = when (config.style) {
        ButtonStyle.Primary -> colors.primary
        ButtonStyle.Secondary -> colors.surface
        ButtonStyle.Destructive -> colors.statusDefault
    }
    val contentColor = if (config.style == ButtonStyle.Secondary) colors.primary else colors.onPrimary

    Button(
        onClick = onClick,
        enabled = config.enabled && !config.loading,
        modifier = modifier
            .then(if (config.fullWidth) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = config.size.minHeight),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = contentColor),
        // The Secondary style is surface-on-surface, so give it a visible border.
        border = if (config.style == ButtonStyle.Secondary) BorderStroke(1.dp, colors.border) else null,
    ) {
        if (config.loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = contentColor,
            )
        } else {
            Text(text = config.text, style = KliqTheme.typography.label)
        }
    }
}

@Preview
@Composable
private fun KliqButtonPreview() {
    KliqTheme {
        KliqButton(config = ButtonConfig(text = "Sign In"), onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
