package com.kliq.loanapp.log

import com.kliq.loanapp.core.common.log.Logger
import timber.log.Timber
import javax.inject.Inject

/**
 * Timber-backed [Logger]. The only place the app's logging abstraction is bound to a concrete
 * framework logger; Timber is planted (debug only) in `KliqApplication`.
 */
class TimberLogger @Inject constructor() : Logger {
    override fun warn(message: String, throwable: Throwable?) = Timber.w(throwable, message)
    override fun error(message: String, throwable: Throwable?) = Timber.e(throwable, message)
}
