package com.kliq.loanapp.core.testing

import com.kliq.loanapp.core.common.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher

/** A [DispatcherProvider] that routes every dispatcher to a single test dispatcher. */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatcherProvider(
    dispatcher: CoroutineDispatcher = StandardTestDispatcher(),
) : DispatcherProvider {
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val main: CoroutineDispatcher = dispatcher
}
