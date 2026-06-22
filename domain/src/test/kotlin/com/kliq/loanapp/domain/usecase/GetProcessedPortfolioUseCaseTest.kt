package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.testing.FakeLoanRepository
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.testLoanProcessor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetProcessedPortfolioUseCaseTest {

    private val processor = testLoanProcessor()

    @Test
    fun `applies the processing pipeline to emitted loans`() = runTest {
        val raw = listOf(LoanFixtures.consumerCredit, LoanFixtures.vehicleFinance)
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(loans = raw), processor)
        assertEquals(processor.process(raw), useCase().first())
    }

    @Test
    fun `propagates a repository failure unchanged`() = runTest {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(error = AppError.AssetMissing), processor)
        val error = runCatching { useCase().first() }.exceptionOrNull()
        assertEquals(AppError.AssetMissing, error)
    }
}
