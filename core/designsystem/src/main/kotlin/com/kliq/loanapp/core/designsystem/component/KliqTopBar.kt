package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** A lightweight title bar with a heading and a trailing actions slot. */
@Composable
fun KliqTopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = KliqTheme.spacing.xl, vertical = KliqTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeadingText(text = title, modifier = Modifier.weight(1f).semantics { heading() })
        actions()
    }
}
