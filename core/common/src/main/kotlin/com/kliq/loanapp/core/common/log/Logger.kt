package com.kliq.loanapp.core.common.log

/**
 * The app's logging seam. Lower layers (data, domain) depend on this abstraction — never on a
 * concrete logger — so the framework-bound implementation (Timber) lives only in the app module
 * and can be swapped or silenced in tests. This keeps the observability story a real, injected
 * boundary rather than scattered `android.util.Log` calls.
 */
interface Logger {
    fun warn(message: String, throwable: Throwable? = null)
    fun error(message: String, throwable: Throwable? = null)
}
