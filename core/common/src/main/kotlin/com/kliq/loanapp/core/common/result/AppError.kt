package com.kliq.loanapp.core.common.result

import java.io.FileNotFoundException
import java.io.IOException

/**
 * Enumerated application error taxonomy. Each case is mapped to a distinct user-facing message and
 * retry affordance in the UI layer. Extends [Exception] so it can flow through [kotlin.Result].
 */
sealed class AppError : Exception() {
    /** loans.json could not be located. */
    data object AssetMissing : AppError()

    /** loans.json was present but malformed. */
    data object ParseFailure : AppError()

    /** A generic read/IO failure. */
    data object Io : AppError()

    /** Authentication failed for a known reason. */
    data class Auth(val reason: AuthReason) : AppError()

    /** Anything unexpected. The original cause is retained for logging. */
    data class Unknown(override val cause: Throwable) : AppError()
}

enum class AuthReason { INVALID_CREDENTIALS, UNKNOWN }

/**
 * Generic mapping for JVM exceptions. Library-specific exceptions (e.g. Gson parse failures) are
 * mapped explicitly at their own boundary in the data layer before reaching here.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppError -> this
    is FileNotFoundException -> AppError.AssetMissing
    is IOException -> AppError.Io
    else -> AppError.Unknown(this)
}
