package com.kliq.loanapp.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Async-load envelope for a data [Flow] (NiA-style). Carries the raw [throwable]; the presentation
 * layer maps it to a user-facing message.
 */
sealed interface Result<out T> {
    data object Loading : Result<Nothing>
    data class Success<out T>(val data: T) : Result<T>
    data class Error(val throwable: Throwable) : Result<Nothing>
}

/** Emits [Result.Loading], then [Result.Success] per value, then [Result.Error] on failure. */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = this
    .map<T, Result<T>> { Result.Success(it) }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }
