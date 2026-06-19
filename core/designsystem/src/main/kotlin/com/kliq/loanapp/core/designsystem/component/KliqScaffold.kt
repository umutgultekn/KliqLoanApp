package com.kliq.loanapp.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

/** Scaffold preset bound to the Kliq background + a snackbar host — the standard screen shell. */
@Composable
fun KliqScaffold(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = KliqTheme.colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = content,
    )
}
