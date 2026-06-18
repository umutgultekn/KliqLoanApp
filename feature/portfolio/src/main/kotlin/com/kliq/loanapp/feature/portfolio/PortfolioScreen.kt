package com.kliq.loanapp.feature.portfolio

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.designsystem.component.ButtonConfig
import com.kliq.loanapp.core.designsystem.component.ButtonStyle
import com.kliq.loanapp.core.designsystem.component.KliqButton
import com.kliq.loanapp.core.designsystem.component.KliqCard
import com.kliq.loanapp.core.designsystem.component.KliqFilterChip
import com.kliq.loanapp.core.designsystem.component.KliqTextButton
import com.kliq.loanapp.core.designsystem.component.LoanCard
import com.kliq.loanapp.core.designsystem.text.asString
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.ui.ObserveAsEvents
import com.kliq.loanapp.core.ui.UiEvent
import com.kliq.loanapp.core.ui.mapper.PortfolioSummaryUi
import kotlinx.coroutines.launch

private enum class PortfolioMode { Loading, Error, Content }

fun NavGraphBuilder.portfolioScreen() {
    composable<KliqRoute.Portfolio> { PortfolioRoute() }
}

@Composable
fun PortfolioRoute(viewModel: PortfolioViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> scope.launch {
                snackbarHostState.showSnackbar(
                    message = event.message.asString(context),
                    actionLabel = event.actionLabel?.asString(context),
                )
            }
        }
    }

    PortfolioScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onFilterSelected = viewModel::onFilterSelected,
        onRetry = viewModel::onRetry,
        onRefresh = viewModel::onRefresh,
        onLogout = viewModel::onLogout,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    state: PortfolioUiState,
    snackbarHostState: SnackbarHostState,
    onFilterSelected: (PortfolioFilter) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
) {
    val colors = KliqTheme.colors
    Scaffold(
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = KliqTheme.spacing.xl, end = KliqTheme.spacing.sm, top = KliqTheme.spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.portfolio_title),
                    style = KliqTheme.typography.heading,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                KliqTextButton(text = stringResource(R.string.portfolio_logout), onClick = onLogout)
            }

            val mode = when {
                state.isLoading -> PortfolioMode.Loading
                state.error != null -> PortfolioMode.Error
                else -> PortfolioMode.Content
            }
            Crossfade(targetState = mode, label = "portfolioMode") { current ->
                when (current) {
                    PortfolioMode.Loading -> CenteredBox { CircularProgressIndicator(color = colors.primary) }
                    PortfolioMode.Error -> CenteredBox {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            state.error?.let { err ->
                                Text(err.asString(), style = KliqTheme.typography.body, color = colors.statusDefault)
                            }
                            Spacer(Modifier.height(KliqTheme.spacing.lg))
                            KliqButton(
                                config = ButtonConfig(text = stringResource(R.string.portfolio_retry), style = ButtonStyle.Secondary),
                                onClick = onRetry,
                            )
                        }
                    }
                    PortfolioMode.Content -> Column(modifier = Modifier.fillMaxSize().padding(horizontal = KliqTheme.spacing.xl)) {
                        SummaryCard(state.summary)
                        Spacer(Modifier.height(KliqTheme.spacing.lg))
                        FilterRow(selected = state.selectedFilter, onFilterSelected = onFilterSelected)
                        Spacer(Modifier.height(KliqTheme.spacing.lg))
                        if (state.cards.isEmpty()) {
                            val emptyMessage = if (state.portfolioEmpty) {
                                R.string.portfolio_empty_all
                            } else {
                                R.string.portfolio_empty_filter
                            }
                            CenteredBox {
                                Text(stringResource(emptyMessage), style = KliqTheme.typography.body, color = colors.textSecondary)
                            }
                        } else {
                            PullToRefreshBox(
                                isRefreshing = state.isRefreshing,
                                onRefresh = onRefresh,
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(KliqTheme.spacing.lg),
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
        color = colors.primary,
        shape = KliqTheme.shapes.cardLarge,
        elevation = KliqTheme.elevation.none,
    ) {
        Text(summary.totalText, style = KliqTheme.typography.heading, color = colors.onPrimary)
        Spacer(Modifier.height(KliqTheme.spacing.sm))
        Text(summary.countText, style = KliqTheme.typography.body, color = colors.onPrimary)
        Text(summary.avgRateText, style = KliqTheme.typography.caption, color = colors.onPrimary)
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
                label = filter.label(),
                selected = filter == selected,
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
