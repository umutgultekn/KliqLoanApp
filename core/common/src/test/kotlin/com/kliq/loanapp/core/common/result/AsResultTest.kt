package com.kliq.loanapp.core.common.result

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AsResultTest {

    @Test fun `emits Loading then Success for each value`() = runTest {
        val results = flowOf(42).asResult().toList()
        assertEquals(listOf(Result.Loading, Result.Success(42)), results)
    }

    @Test fun `emits Loading then Error when the source throws`() = runTest {
        val boom = RuntimeException("boom")
        val results = flow<Int> { throw boom }.asResult().toList()
        assertEquals(Result.Loading, results[0])
        val error = results[1]
        assertTrue(error is Result.Error)
        assertSame(boom, (error as Result.Error).throwable)
    }
}
