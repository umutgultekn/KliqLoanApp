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
import com.kliq.loanapp.core.ui.BaseViewModel
import com.kliq.loanapp.core.ui.error.asUiText
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.repository.SessionRepository
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    // Loaded once; processing always runs on the freshly-fetched raw list (deterministic).
    private val loadState: StateFlow<LoadState> = flow { emit(loadOnce()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LoadState.Loading)

    init {
        combine(loadState, selectedFilter, ::reduce)
            .onEach { state -> setState { state } }
            .launchIn(viewModelScope)
    }

    fun onFilterSelected(filter: PortfolioFilter) {
        savedStateHandle[KEY_FILTER] = filter
    }

    fun onLogout() = launchSafe {
        sessionRepository.setLoggedIn(false)
        navigator.navigate(NavCommand.To(KliqRoute.Login, popUpTo = KliqRoute.Portfolio, inclusive = true))
    }

    private suspend fun loadOnce(): LoadState = getProcessedPortfolio().fold(
        onSuccess = { LoadState.Success(it) },
        onFailure = { LoadState.Error(it.toAppError()) },
    )

    private fun reduce(load: LoadState, filter: PortfolioFilter): PortfolioUiState = when (load) {
        LoadState.Loading -> PortfolioUiState(isLoading = true, selectedFilter = filter)
        is LoadState.Error -> PortfolioUiState(isLoading = false, error = load.error.asUiText(), selectedFilter = filter)
        is LoadState.Success -> {
            val filtered = load.loans.filter(filter::matches)
            PortfolioUiState(
                isLoading = false,
                cards = mapper.toCards(filtered),
                // The summary card reflects the WHOLE portfolio, not the current filter — the filter
                // only narrows the list below it.
                summary = mapper.summary(load.loans),
                selectedFilter = filter,
                isEmpty = load.loans.isEmpty(),
            )
        }
    }

    private sealed interface LoadState {
        data object Loading : LoadState
        data class Success(val loans: List<Loan>) : LoadState
        data class Error(val error: AppError) : LoadState
    }

    private companion object {
        const val KEY_FILTER = "portfolio_filter"
    }
}
