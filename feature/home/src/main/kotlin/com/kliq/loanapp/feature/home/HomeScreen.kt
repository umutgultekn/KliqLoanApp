package com.kliq.loanapp.feature.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.designsystem.component.ChipConfig
import com.kliq.loanapp.core.designsystem.component.ConfirmDialog
import com.kliq.loanapp.core.designsystem.component.EmptyState
import com.kliq.loanapp.core.designsystem.component.KliqCard
import com.kliq.loanapp.core.designsystem.component.KliqFilterChip
import com.kliq.loanapp.core.designsystem.component.KliqListSkeleton
import com.kliq.loanapp.core.designsystem.component.KliqScaffold
import com.kliq.loanapp.core.designsystem.component.KliqText
import com.kliq.loanapp.core.designsystem.component.KliqTextButton
import com.kliq.loanapp.core.designsystem.component.KliqTextStyle
import com.kliq.loanapp.core.designsystem.component.KliqTopBar
import com.kliq.loanapp.core.designsystem.component.LoanCard
import com.kliq.loanapp.core.designsystem.component.SecondaryButton
import com.kliq.loanapp.core.designsystem.text.asString
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi
import com.kliq.loanapp.core.ui.rememberSnackbarEvents

private enum class HomeMode { Loading, Error, Content }

/** Minimum comfortable width for a loan card; the grid fits as many columns as the width allows. */
private val LoanCardMinWidth = 300.dp

fun NavGraphBuilder.homeScreen() {
    composable<KliqRoute.Home> { HomeRoute() }
}

@Composable
fun HomeRoute(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarEvents(viewModel.events)

    HomeScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onFilterSelected = viewModel::onFilterSelected,
        onRetry = viewModel::onRetry,
        onRefresh = viewModel::onRefresh,
        onLogoutClicked = viewModel::onLogoutClicked,
        onLogoutConfirmed = viewModel::onLogoutConfirmed,
        onLogoutDismissed = viewModel::onLogoutDismissed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onFilterSelected: (PortfolioFilter) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLogoutClicked: () -> Unit,
    onLogoutConfirmed: () -> Unit,
    onLogoutDismissed: () -> Unit,
) {
    val colors = KliqTheme.colors

    if (state.showLogoutConfirm) {
        ConfirmDialog(
            title = stringResource(R.string.portfolio_logout_title),
            message = stringResource(R.string.portfolio_logout_message),
            confirmLabel = stringResource(R.string.portfolio_logout_confirm),
            dismissLabel = stringResource(R.string.portfolio_logout_cancel),
            onConfirm = onLogoutConfirmed,
            onDismiss = onLogoutDismissed,
        )
    }

    KliqScaffold(snackbarHostState = snackbarHostState) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            KliqTopBar(title = stringResource(R.string.portfolio_title)) {
                KliqTextButton(text = stringResource(R.string.portfolio_logout), onClick = onLogoutClicked)
            }

            val mode = when {
                state.isLoading -> HomeMode.Loading
                state.error != null -> HomeMode.Error
                else -> HomeMode.Content
            }
            Crossfade(targetState = mode, label = "homeMode") { current ->
                when (current) {
                    HomeMode.Loading -> KliqListSkeleton()
                    HomeMode.Error -> CenteredBox {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            state.error?.let { err ->
                                KliqText(err.asString(), style = KliqTextStyle.Body, color = colors.statusDefault)
                            }
                            Spacer(Modifier.height(KliqTheme.spacing.lg))
                            SecondaryButton(text = stringResource(R.string.portfolio_retry), onClick = onRetry)
                        }
                    }
                    HomeMode.Content -> Column(modifier = Modifier.fillMaxSize().padding(horizontal = KliqTheme.spacing.xl)) {
                        SummaryCard(state.summary)
                        Spacer(Modifier.height(KliqTheme.spacing.lg))
                        FilterRow(selected = state.selectedFilter, onFilterSelected = onFilterSelected)
                        Spacer(Modifier.height(KliqTheme.spacing.lg))
                        if (state.cards.isEmpty()) {
                            CenteredBox {
                                if (state.portfolioEmpty) {
                                    EmptyState(
                                        title = stringResource(R.string.portfolio_empty_all_title),
                                        message = stringResource(R.string.portfolio_empty_all),
                                    )
                                } else {
                                    EmptyState(
                                        title = stringResource(R.string.portfolio_empty_filter_title),
                                        message = stringResource(R.string.portfolio_empty_filter),
                                        actionLabel = stringResource(R.string.portfolio_show_all),
                                        onAction = { onFilterSelected(PortfolioFilter.ALL) },
                                    )
                                }
                            }
                        } else {
                            PullToRefreshBox(
                                isRefreshing = state.isRefreshing,
                                onRefresh = onRefresh,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                // Adaptive columns: the grid fits as many >= LoanCardMinWidth cells as
                                // the available width allows (1 on phones, 2+ on tablets/landscape) —
                                // no BoxWithConstraints, no manual width breakpoint or column count.
                                LazyVerticalGrid(
                                    columns = GridCells.Adaptive(minSize = LoanCardMinWidth),
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(KliqTheme.spacing.lg),
                                    horizontalArrangement = Arrangement.spacedBy(KliqTheme.spacing.lg),
                                    contentPadding = PaddingValues(bottom = KliqTheme.spacing.xxxl),
                                ) {
                                    items(state.cards, key = { it.id }) { card ->
                                        LoanCard(config = card, modifier = Modifier.animateItem())
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: PortfolioSummaryUi) {
    val colors = KliqTheme.colors
    KliqCard(
        modifier = Modifier.semantics(mergeDescendants = true) {},
        color = colors.primary,
        shape = KliqTheme.shapes.cardLarge,
        elevation = KliqTheme.elevation.raised,
    ) {
        KliqText(
            text = stringResource(R.string.portfolio_total_label),
            style = KliqTextStyle.Caption,
            color = colors.onPrimaryMuted,
        )
        KliqText(summary.totalText, style = KliqTextStyle.Heading, color = colors.onPrimary)
        Spacer(Modifier.height(KliqTheme.spacing.sm))
        KliqText(summary.countText.asString(), style = KliqTextStyle.Body, color = colors.onPrimary)
        KliqText(summary.avgRateText.asString(), style = KliqTextStyle.Caption, color = colors.onPrimary)
    }
}

@Composable
private fun FilterRow(selected: PortfolioFilter, onFilterSelected: (PortfolioFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(KliqTheme.spacing.md),
    ) {
        PortfolioFilter.entries.forEach { filter ->
            KliqFilterChip(
                config = ChipConfig(label = filter.label(), selected = filter == selected),
                onClick = { onFilterSelected(filter) },
            )
        }
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

@Composable
private fun PortfolioFilter.label(): String = stringResource(
    when (this) {
        PortfolioFilter.ALL -> R.string.filter_all
        PortfolioFilter.ACTIVE -> R.string.filter_active
        PortfolioFilter.OVERDUE -> R.string.filter_overdue
        PortfolioFilter.DEFAULT -> R.string.filter_default
        PortfolioFilter.PAID -> R.string.filter_paid
    },
)
