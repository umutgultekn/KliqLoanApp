package com.kliq.loanapp.data.repository

import com.google.gson.JsonSyntaxException
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.testing.FakeLoanService
import com.kliq.loanapp.core.testing.LoanFixtures
import com.kliq.loanapp.core.testing.TestDispatcherProvider
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.FileNotFoundException
import java.io.IOException

class LoanRepositoryImplTest {

    // Build the repository with a dispatcher bound to runTest's scheduler so withContext(io) runs.
    private fun TestScope.repository(service: FakeLoanService) =
        LoanRepositoryImpl(service, TestDispatcherProvider(StandardTestDispatcher(testScheduler)))

    @Test fun `returns loans on success`() = runTest {
        val service = FakeLoanService(loans = listOf(LoanFixtures.consumerCredit))
        val result = repository(service).getLoans()
        assertEquals(listOf(LoanFixtures.consumerCredit), result.getOrNull())
    }

    @Test fun `maps malformed json to ParseFailure`() = runTest {
        val result = repository(FakeLoanService(error = JsonSyntaxException("bad"))).getLoans()
        assertEquals(AppError.ParseFailure, result.exceptionOrNull())
    }

    @Test fun `maps missing asset to AssetMissing`() = runTest {
        val result = repository(FakeLoanService(error = FileNotFoundException())).getLoans()
        assertEquals(AppError.AssetMissing, result.exceptionOrNull())
    }

    @Test fun `maps io failure to Io`() = runTest {
        val result = repository(FakeLoanService(error = IOException())).getLoans()
        assertEquals(AppError.Io, result.exceptionOrNull())
    }

    @Test fun `wraps unexpected errors as Unknown`() = runTest {
        val boom = IllegalStateException("boom")
        val result = repository(FakeLoanService(error = boom)).getLoans()
        assertTrue(result.exceptionOrNull() is AppError.Unknown)
    }
}
