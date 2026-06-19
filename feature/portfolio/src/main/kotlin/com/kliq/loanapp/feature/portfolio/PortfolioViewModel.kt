package com.kliq.loanapp.feature.portfolio

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.result.toAppError
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.model.PortfolioSummary
import com.kliq.loanapp.core.ui.BaseViewModel
import com.kliq.loanapp.core.ui.error.asUiText
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.repository.SessionRepository
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val getProcessedPortfolio: GetProcessedPortfolioUseCase,
    private val mapper: LoanPresentationMapper,
    private val sessionRepository: SessionRepository,
    private val navigator: Navigator,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<PortfolioUiState>(PortfolioUiState()) {

    // Persisted across process death; the selected filter survives restoration.
    private val selectedFilter = savedStateHandle.getStateFlow(KEY_FILTER, PortfolioFilter.ALL)

    // Processing always runs on the freshly-fetched raw list (deterministic across reloads).
    private val loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    private val refreshing = MutableStateFlow(false)
    private val logoutConfirm = MutableStateFlow(false)

    init {
        reload(showLoading = true)
        combine(loadState, selectedFilter, refreshing, logoutConfirm, ::reduce)
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
            sessionRepository.setLoggedIn(false)
            navigator.navigate(NavCommand.To(KliqRoute.Login, popUpTo = KliqRoute.Portfolio, inclusive = true))
        }
    }

    private fun reload(showLoading: Boolean): Job = launchSafe {
        if (showLoading) loadState.value = LoadState.Loading else refreshing.value = true
        loadState.value = loadOnce()
        refreshing.value = false
    }

    private suspend fun loadOnce(): LoadState = getProcessedPortfolio().fold(
        onSuccess = { LoadState.Success(it) },
        onFailure = { LoadState.Error(it.toAppError()) },
    )

    private fun reduce(
        load: LoadState,
        filter: PortfolioFilter,
        isRefreshing: Boolean,
        showLogoutConfirm: Boolean,
    ): PortfolioUiState = when (load) {
        LoadState.Loading -> PortfolioUiState(
            isLoading = true,
            selectedFilter = filter,
            showLogoutConfirm = showLogoutConfirm,
        )
        is LoadState.Error -> PortfolioUiState(
            isLoading = false,
            error = load.error.asUiText(),
            selectedFilter = filter,
            isRefreshing = isRefreshing,
            showLogoutConfirm = showLogoutConfirm,
        )
        is LoadState.Success -> {
            val filtered = load.loans.filter(filter::matches)
            PortfolioUiState(
                isLoading = false,
                cards = mapper.toCards(filtered),
                // The summary card reflects the WHOLE portfolio; the filter only narrows the list.
                summary = mapper.summary(PortfolioSummary.from(load.loans)),
                selectedFilter = filter,
                portfolioEmpty = load.loans.isEmpty(),
                isRefreshing = isRefreshing,
                showLogoutConfirm = showLogoutConfirm,
            )
        }
    }

    private sealed interface LoadState {
        data object Loading : LoadState
        data class Success(val loans: List<Loan>) : LoadState
        data class Error(val error: AppError) : LoadState
    }

    internal companion object {
        const val KEY_FILTER = "portfolio_filter"
    }
}
