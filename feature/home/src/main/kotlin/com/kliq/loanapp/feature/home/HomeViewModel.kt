package com.kliq.loanapp.feature.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.common.result.AppError
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
import kotlinx.coroutines.flow.combine
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

    // Processing always runs on the freshly-fetched raw list (deterministic across reloads).
    // null = not loaded yet; the full-screen loading state comes from the shared BaseViewModel.isLoading.
    private val loadState = MutableStateFlow<LoadState?>(null)
    private val refreshing = MutableStateFlow(false)
    private val logoutConfirm = MutableStateFlow(false)

    init {
        reload(showLoading = true)
        combine(loadState, isLoading, selectedFilter, refreshing, logoutConfirm, ::reduce)
            .onEach { state -> setState { state } }
            .launchIn(viewModelScope)
    }

    fun onFilterSelected(filter: PortfolioFilter) {
        savedStateHandle[KEY_FILTER] = filter
    }

    /** Full reload with a loading state — used by the error-state retry button. */
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

    private fun reload(showLoading: Boolean): Job = launchSafe(loading = showLoading) {
        if (!showLoading) refreshing.value = true
        loadState.value = loadOnce()
        refreshing.value = false
    }

    private suspend fun loadOnce(): LoadState = getProcessedPortfolio().fold(
        onSuccess = { LoadState.Success(it) },
        onFailure = { LoadState.Error(it.toAppError()) },
    )

    private fun reduce(
        load: LoadState?,
        isLoading: Boolean,
        filter: PortfolioFilter,
        isRefreshing: Boolean,
        showLogoutConfirm: Boolean,
    ): HomeUiState {
        // Full-screen loading is the shared BaseViewModel flag (initial fetch + retry); a pull-to-
        // refresh (isLoading = false) keeps the current content and shows isRefreshing instead.
        val content: UiState<HomeData> = if (isLoading || load == null) {
            UiState.Loading
        } else {
            when (load) {
                is LoadState.Error -> UiState.Error(load.error.asUiText())
                is LoadState.Success -> UiState.Content(
                    HomeData(
                        cards = mapper.toCards(load.loans.filter(filter::matches)),
                        // The summary card reflects the WHOLE portfolio; the filter only narrows the list.
                        summary = mapper.summary(PortfolioSummary.from(load.loans)),
                        portfolioEmpty = load.loans.isEmpty(),
                    ),
                )
            }
        }
        return HomeUiState(
            content = content,
            selectedFilter = filter,
            isRefreshing = isRefreshing,
            showLogoutConfirm = showLogoutConfirm,
        )
    }

    private sealed interface LoadState {
        data class Success(val loans: List<Loan>) : LoadState
        data class Error(val error: AppError) : LoadState
    }

    internal companion object {
        const val KEY_FILTER = "portfolio_filter"
    }
}
