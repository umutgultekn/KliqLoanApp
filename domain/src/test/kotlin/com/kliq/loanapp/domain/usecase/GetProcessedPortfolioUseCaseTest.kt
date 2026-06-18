package com.kliq.loanapp.domain.usecase

import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.testing.FakeLoanRepository
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.testLoanProcessor
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetProcessedPortfolioUseCaseTest {

    private val processor = testLoanProcessor()

    @Test
    fun `applies the processing pipeline to fetched loans`() = runTest {
        val raw = listOf(LoanFixtures.consumerCredit, LoanFixtures.vehicleFinance)
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.success(raw)), processor)
        assertEquals(processor.process(raw), useCase().getOrNull())
    }

    @Test
    fun `passes repository failure through unchanged`() = runTest {
        val useCase = GetProcessedPortfolioUseCase(FakeLoanRepository(Result.failure(AppError.AssetMissing)), processor)
        assertEquals(AppError.AssetMissing, useCase().exceptionOrNull())
    }
}
