package com.kliq.loanapp.data.repository

import com.google.gson.JsonSyntaxException
import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.testing.RecordingLogger
import com.kliq.loanapp.core.testing.TestDispatcherProvider
import com.kliq.loanapp.data.datasource.LoanRemoteDataSource
import com.kliq.loanapp.data.dto.LoanDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.FileNotFoundException
import java.io.IOException

class LoanRepositoryImplTest {

    /** Fake remote data source: returns [dtos] or throws [error]. */
    private class FakeRemote(
        private val dtos: List<LoanDto> = emptyList(),
        private val error: Throwable? = null,
    ) : LoanRemoteDataSource {
        override suspend fun fetchLoans(): List<LoanDto> {
            error?.let { throw it }
            return dtos
        }
    }

    private val logger = RecordingLogger()

    // Bind the repository to a dispatcher on runTest's scheduler so flowOn(io) runs deterministically.
    private fun TestScope.repository(remote: LoanRemoteDataSource) =
        LoanRepositoryImpl(remote, TestDispatcherProvider(StandardTestDispatcher(testScheduler)), logger)

    private val validDto = LoanDto("Consumer Credit", 8_500.0, 2.9, "active", 45, "personal")
    private val malformedDto =
        LoanDto(name = null, principalAmount = 1.0, interestRate = 1.0, status = "active", dueInDays = 1, type = "auto")

    private suspend fun TestScope.loadError(remote: LoanRemoteDataSource): Throwable? =
        runCatching { repository(remote).getLoans().first() }.exceptionOrNull()

    @Test fun `maps DTOs to domain loans`() = runTest {
        val loans = repository(FakeRemote(listOf(validDto))).getLoans().first()
        assertEquals(1, loans.size)
        assertEquals("Consumer Credit", loans[0].name)
    }

    @Test fun `drops malformed records and logs the dropped count`() = runTest {
        val loans = repository(FakeRemote(listOf(validDto, malformedDto))).getLoans().first()
        assertEquals(1, loans.size)
        assertEquals(1, logger.warnings.size)
    }

    @Test fun `maps malformed json to ParseFailure`() = runTest {
        assertEquals(AppError.ParseFailure, loadError(FakeRemote(error = JsonSyntaxException("bad"))))
    }

    @Test fun `maps missing asset to AssetMissing`() = runTest {
        assertEquals(AppError.AssetMissing, loadError(FakeRemote(error = FileNotFoundException())))
    }

    @Test fun `maps io failure to Io`() = runTest {
        assertEquals(AppError.Io, loadError(FakeRemote(error = IOException())))
    }

    @Test fun `wraps unexpected errors as Unknown`() = runTest {
        assertTrue(loadError(FakeRemote(error = IllegalStateException("boom"))) is AppError.Unknown)
    }
}
