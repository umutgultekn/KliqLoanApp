package com.kliq.loanapp.core.designsystem.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** Reusable content-shaped loading skeleton: an optional header block + [itemCount] row blocks. */
@Composable
fun KliqListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 4,
    showHeader: Boolean = true,
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = KliqTheme.spacing.xl)) {
        if (showHeader) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(KliqTheme.sizes.skeletonHeader), shape = KliqTheme.shapes.cardLarge)
            Spacer(Modifier.height(KliqTheme.spacing.lg))
        }
        repeat(itemCount) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(KliqTheme.sizes.skeletonRow))
            Spacer(Modifier.height(KliqTheme.spacing.lg))
        }
    }
}

/** A pulsing placeholder block used to build loading skeletons. */
@Composable
fun ShimmerBox(modifier: Modifier = Modifier, shape: Shape = KliqTheme.shapes.card) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 800), repeatMode = RepeatMode.Reverse),
        label = "shimmerAlpha",
    )
    Box(modifier = modifier.clip(shape).background(KliqTheme.colors.border.copy(alpha = alpha)))
}

/** Centered empty/zero-state with an optional call to action. */
@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(KliqTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KliqText(text = title, style = KliqTextStyle.Title)
        Spacer(Modifier.height(KliqTheme.spacing.sm))
        KliqText(
            text = message,
            style = KliqTextStyle.Body,
            color = KliqTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(KliqTheme.spacing.lg))
            KliqButton(config = ButtonConfig(text = actionLabel, style = ButtonStyle.Secondary), onClick = onAction)
        }
    }
}
