package com.kliq.loanapp.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.common.result.Result
import com.kliq.loanapp.core.common.result.asResult
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.model.PortfolioSummary
import com.kliq.loanapp.core.ui.BaseViewModel
import com.kliq.loanapp.core.ui.UiState
import com.kliq.loanapp.core.ui.error.asUiText
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase
import com.kliq.loanapp.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProcessedPortfolio: GetProcessedPortfolioUseCase,
    private val mapper: LoanPresentationMapper,
    private val logout: LogoutUseCase,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<HomeUiState>(HomeUiState()) {

    // Persisted across process death; the selected filter survives restoration.
    private val selectedFilter = savedStateHandle.getStateFlow(KEY_FILTER, PortfolioFilter.ALL)

    // The raw load phase as ONE stream that starts at Loading — the canonical pattern (no nullable
    // "not-loaded-yet" sentinel, no separate loading flag). Holds the unprocessed loans so a filter
    // change re-derives cards without re-fetching. Pull-to-refresh keeps the current content visible
    // via [refreshing] instead of flipping back to Loading.
    private val loadResult = MutableStateFlow<UiState<List<Loan>>>(UiState.Loading)
    private val refreshing = MutableStateFlow(false)
    private val logoutConfirm = MutableStateFlow(false)

    init {
        reload(showLoading = true)
        combine(loadResult, selectedFilter, refreshing, logoutConfirm, ::reduce)
            .onEach { state -> setState { state } }
            .launchIn(viewModelScope)
    }

    fun onFilterSelected(filter: PortfolioFilter) {
        savedStateHandle[KEY_FILTER] = filter
    }

    /** Full reload that shows the loading phase — used by the error-state retry button. */
    fun onRetry() = reload(showLoading = true)

    /** Background reload that keeps the current content — used by pull-to-refresh. */
    fun onRefresh() = reload(showLoading = false)

    fun onLogoutClicked() { logoutConfirm.value = true }
    fun onLogoutDismissed() { logoutConfirm.value = false }

    fun onLogoutConfirmed() {
        logoutConfirm.value = false
        launchSafe {
            logout()
            navigator.navigate(NavCommand.ToLogin)
        }
    }

    // Bridge the one-shot use case into a Flow + asResult so the load goes through the shared,
    // canonical Loading/Success/Error pipeline (ready for a real Flow-based network/DB source).
    // asResult emits Loading first: on a full load it shows the loading phase; on pull-to-refresh
    // (showLoading = false) it only flips isRefreshing so the current content stays visible.
    private fun reload(showLoading: Boolean): Job = launchSafe {
        flow { emit(getProcessedPortfolio().getOrThrow()) }
            .asResult()
            .collect { result ->
                when (result) {
                    Result.Loading ->
                        if (showLoading) loadResult.value = UiState.Loading else refreshing.value = true
                    is Result.Success -> {
                        loadResult.value = UiState.Content(result.data)
                        refreshing.value = false
                    }
                    is Result.Error -> {
                        loadResult.value = UiState.Error(result.throwable.toAppError().asUiText())
                        refreshing.value = false
                    }
                }
            }
    }

    private fun reduce(
        result: UiState<List<Loan>>,
        filter: PortfolioFilter,
        isRefreshing: Boolean,
        showLogoutConfirm: Boolean,
    ): HomeUiState {
        val content: UiState<HomeData> = when (result) {
            UiState.Loading -> UiState.Loading
            is UiState.Error -> result
            is UiState.Content -> UiState.Content(
                HomeData(
                    cards = mapper.toCards(result.data.filter(filter::matches)),
                    // The summary card reflects the WHOLE portfolio; the filter only narrows the list.
                    summary = mapper.summary(PortfolioSummary.from(result.data)),
                    portfolioEmpty = result.data.isEmpty(),
                ),
            )
        }
        return HomeUiState(
            content = content,
            selectedFilter = filter,
            isRefreshing = isRefreshing,
            showLogoutConfirm = showLogoutConfirm,
        )
    }

    internal companion object {
        const val KEY_FILTER = "portfolio_filter"
    }
}
