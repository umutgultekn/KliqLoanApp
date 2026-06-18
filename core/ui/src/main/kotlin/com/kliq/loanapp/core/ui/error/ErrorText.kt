package com.kliq.loanapp.core.ui.error

import com.kliq.loanapp.core.common.result.AppError
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.ui.R

/** Maps each [AppError] case to a distinct, localized user-facing message. */
fun AppError.asUiText(): UiText = when (this) {
    AppError.AssetMissing -> UiText.res(R.string.kliq_error_asset_missing)
    AppError.ParseFailure -> UiText.res(R.string.kliq_error_parse_failure)
    AppError.EmptyPortfolio -> UiText.res(R.string.kliq_error_empty_portfolio)
    AppError.Io -> UiText.res(R.string.kliq_error_io)
    is AppError.Auth -> UiText.res(R.string.kliq_error_auth_invalid)
    is AppError.Unknown -> UiText.res(R.string.kliq_error_unknown)
}

/** True when the error is retryable (transient) vs. a permanent data problem. */
val AppError.isRetryable: Boolean
    get() = this is AppError.Io || this is AppError.Unknown
