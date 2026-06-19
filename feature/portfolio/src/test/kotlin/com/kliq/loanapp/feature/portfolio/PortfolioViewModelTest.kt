package com.kliq.loanapp.feature.portfolio

import androidx.lifecycle.SavedStateHandle
import com.kliq.loanapp.core.common.format.DefaultLoanFormatter
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.common.navigation.NavCommand
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.model.Loan
import com.kliq.loanapp.core.model.PortfolioFilter
import com.kliq.loanapp.core.testing.FakeLoanRepository
import com.kliq.loanapp.core.testing.FakeNavigator
import com.kliq.loanapp.core.testing.FakeSessionRepository
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.MainDispatcherRule
import com.kliq.loanapp.core.testing.testLoanProcessor
import com.kliq.loanapp.core.ui.mapper.LoanPresentationMapper
import com.kliq.loanapp.domain.usecase.GetProcessedPortfolioUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PortfolioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val navigator = FakeNavigator()
    private val session = FakeSessionRepository(initial = true)
    private val mapper = LoanPresentationMapper(DefaultLoanFormatter())

    private fun viewModel(loans: List<Loan>): PortfolioViewModel {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.success(loans)), testLoanProcessor())
        return PortfolioViewModel(useCase, mapper, session, navigator, SavedStateHandle())
    }

    // After processing: Consumer Credit stays ACTIVE, Vehicle Finance stays OVERDUE, Commercial Credit stays DEFAULT.
    private val sample = listOf(
        LoanFixtures.consumerCredit,
        LoanFixtures.vehicleFinance,
        LoanFixtures.commercialCredit,
    )

    @Test
    fun `loads all processed loans`() = runTest {
        val vm = viewModel(sample)
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.cards.size)
    }

    @Test
    fun `selecting a status filter narrows the list`() = runTest {
        val vm = viewModel(sample)
        vm.onFilterSelected(PortfolioFilter.ACTIVE)
        assertEquals(1, vm.uiState.value.cards.size)

        vm.onFilterSelected(PortfolioFilter.DEFAULT)
        assertEquals(1, vm.uiState.value.cards.size)

        vm.onFilterSelected(PortfolioFilter.PAID)
        assertEquals(0, vm.uiState.value.cards.size)
    }

    @Test
    fun `summary reflects the whole portfolio regardless of the active filter`() = runTest {
        val vm = viewModel(sample)
        val fullSummary = vm.uiState.value.summary.countText
        assertEquals(3, (fullSummary as UiText.Plural).quantity)

        vm.onFilterSelected(PortfolioFilter.ACTIVE)
        assertEquals(1, vm.uiState.value.cards.size)
        // Summary stays on the full portfolio even though the list narrowed to 1.
        assertEquals(fullSummary, vm.uiState.value.summary.countText)
    }

    @Test
    fun `load failure produces an error state`() = runTest {
        val useCase =
            GetProcessedPortfolioUseCase(FakeLoanRepository(Result.failure(AppError.AssetMissing)), testLoanProcessor())
        val vm = PortfolioViewModel(useCase, mapper, session, navigator, SavedStateHandle())
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertEquals(0, state.cards.size)
    }

    @Test
    fun `an empty portfolio is flagged`() = runTest {
        val vm = viewModel(emptyList())
        assertTrue(vm.uiState.value.cards.isEmpty())
        assertTrue(vm.uiState.value.portfolioEmpty)
    }

    @Test
    fun `restores the persisted filter from SavedStateHandle`() = runTest {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.success(sample)), testLoanProcessor())
        val vm = PortfolioViewModel(
            useCase, mapper, session, navigator,
            SavedStateHandle(mapOf("portfolio_filter" to PortfolioFilter.ACTIVE)),
        )
        assertEquals(PortfolioFilter.ACTIVE, vm.uiState.value.selectedFilter)
        assertEquals(1, vm.uiState.value.cards.size)
    }

    @Test
    fun `retry recovers from a load error`() = runTest {
        val repo = FakeLoanRepository(Result.failure(AppError.AssetMissing))
        val vm = PortfolioViewModel(
            GetProcessedPortfolioUseCase(repo, testLoanProcessor()), mapper, session, navigator, SavedStateHandle(),
        )
        assertNotNull(vm.uiState.value.error)

        repo.result = Result.success(sample)
        vm.onRetry()

        assertNull(vm.uiState.value.error)
        assertEquals(3, vm.uiState.value.cards.size)
    }

    @Test
    fun `logout clicked shows confirmation without leaving`() = runTest {
        val vm = viewModel(sample)
        vm.onLogoutClicked()
        assertTrue(vm.uiState.value.showLogoutConfirm)
        assertTrue(session.current) // still logged in — only confirmed logout signs out
        assertTrue(navigator.received.isEmpty())
    }

    @Test
    fun `confirming logout clears the session and navigates to login`() = runTest {
        val vm = viewModel(sample)
        vm.onLogoutClicked()
        vm.onLogoutConfirmed()
        assertFalse(vm.uiState.value.showLogoutConfirm)
        assertFalse(session.current)
        assertEquals(
            NavCommand.To(KliqRoute.Login, popUpTo = KliqRoute.Portfolio, inclusive = true),
            navigator.last,
        )
    }
}
