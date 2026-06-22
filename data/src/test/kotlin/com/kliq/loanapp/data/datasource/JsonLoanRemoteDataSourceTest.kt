package com.kliq.loanapp.data.datasource

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Robolectric test reading the REAL bundled `loans.json` so production parsing can't silently drift
 * (record count + raw status histogram + the four loan-type wire values). Domain mapping is covered
 * by LoanRepositoryImplTest + LoanMapperTest.
 */
@RunWith(RobolectricTestRunner::class)
class JsonLoanRemoteDataSourceTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val dataSource = JsonLoanRemoteDataSource(context, Gson())

    @Test
    fun `parses all 29 records from the asset`() = runTest {
        assertEquals(29, dataSource.fetchLoans().size)
    }

    @Test
    fun `raw status histogram matches the source file`() = runTest {
        val byStatus = dataSource.fetchLoans().groupingBy { it.status }.eachCount()
        assertEquals(22, byStatus["active"])
        assertEquals(4, byStatus["overdue"])
        assertEquals(3, byStatus["default"])
        assertEquals(null, byStatus["paid"]) // no paid records in source
    }

    @Test
    fun `all four loan-type wire values are present`() = runTest {
        val types = dataSource.fetchLoans().map { it.type }.toSet()
        assertEquals(setOf("personal", "mortgage", "auto", "business"), types)
    }
}
