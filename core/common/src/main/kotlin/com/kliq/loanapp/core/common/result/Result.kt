package com.kliq.loanapp.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Async-load envelope for a data [Flow] (the Now-in-Android-style wrapper). Carries the raw
 * [throwable] at the data/domain boundary; the presentation layer maps it to a UI state with a
 * user-facing message. Reusable across every data-loading screen.
 */
sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable) : Result<Nothing>
}

/**
 * Wraps a data [Flow] so it emits [Result.Loading] first, then [Result.Success] for each value, and
 * [Result.Error] if it throws. The canonical building block for observable data sources (network /
 * DB / DataStore): a ViewModel collects `repository.something().asResult()` and maps each phase to
 * its UI state — no manual try/catch or "loading" bookkeeping at the call site.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = this
    .map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }
