package com.kliq.loanapp.data.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import com.kliq.loanapp.core.model.LoanStatus
import com.kliq.loanapp.core.model.LoanType
import com.kliq.loanapp.core.testing.RecordingLogger
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Robolectric test reading the REAL bundled `loans.json` so production parsing can't silently drift
 * (count + status histogram + type coverage of the raw, unprocessed data).
 */
@RunWith(RobolectricTestRunner::class)
class MockLoanServiceTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val service = MockLoanService(context, Gson(), RecordingLogger())

    @Test
    fun `parses all 29 records`() = runTest {
        assertEquals(29, service.fetchLoans().size)
    }

    @Test
    fun `raw status histogram matches the source file`() = runTest {
        val byStatus = service.fetchLoans().groupingBy { it.status }.eachCount()
        assertEquals(22, byStatus[LoanStatus.ACTIVE])
        assertEquals(4, byStatus[LoanStatus.OVERDUE])
        assertEquals(3, byStatus[LoanStatus.DEFAULT])
        assertEquals(null, byStatus[LoanStatus.PAID]) // no paid records in source
    }

    @Test
    fun `all four loan types are present`() = runTest {
        assertEquals(LoanType.entries.toSet(), service.fetchLoans().map { it.type }.toSet())
    }
}
