package com.kliq.loanapp.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Injectable abstraction over coroutine dispatchers so production code uses real dispatchers while
 * tests substitute a single [kotlinx.coroutines.test.TestDispatcher].
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
}
