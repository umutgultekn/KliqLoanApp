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

    // The raw load phase as ONE stream that starts at Loading (the canonical pattern — no nullable
    // "not-loaded-yet" sentinel). Internally it's a [Result] (data envelope, carries the Throwable);
    // reduce maps it to the presentation [UiState]. Holds the unprocessed loans so a filter change
    // re-derives cards without re-fetching. Pull-to-refresh keeps content visible via [refreshing].
    private val loadResult = MutableStateFlow<Result<List<Loan>>>(Result.Loading)
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

    // Collect the use case's reactive stream through asResult: Loading on start, then Success/Error.
    // On a full load the Loading phase is shown; on pull-to-refresh (showLoading = false) it only
    // flips isRefreshing so the current content stays visible.
    private fun reload(showLoading: Boolean): Job = launchSafe {
        getProcessedPortfolio()
            .asResult()
            .collect { result ->
                when (result) {
                    Result.Loading -> if (showLoading) loadResult.value = Result.Loading else refreshing.value = true
                    else -> {
                        loadResult.value = result
                        refreshing.value = false
                    }
                }
            }
    }

    // Pure mapping from the data envelope [Result] to the presentation [UiState]; the error Throwable
    // becomes a user-facing message here, in the presentation layer.
    private fun reduce(
        result: Result<List<Loan>>,
        filter: PortfolioFilter,
        isRefreshing: Boolean,
        showLogoutConfirm: Boolean,
    ): HomeUiState {
        val content: UiState<HomeData> = when (result) {
            Result.Loading -> UiState.Loading
            is Result.Error -> UiState.Error(result.throwable.toAppError().asUiText())
            is Result.Success -> UiState.Content(
                HomeData(
                    cards = mapper.toCards(result.data.filter(filter::matches)),
                    // The summary card reflects the WHOLE portfolio; the filter only narrows the list.
                    summary = mapper.summary(PortfolioSummary.from(result.data)),
                    portfolioEmpty = result.data.isEmpty(),
                    // Refresh is meaningful only with content, so it rides on the Content phase.
                    isRefreshing = isRefreshing,
                ),
            )
        }
        return HomeUiState(
            content = content,
            selectedFilter = filter,
            showLogoutConfirm = showLogoutConfirm,
        )
    }

    internal companion object {
        const val KEY_FILTER = "portfolio_filter"
    }
}
