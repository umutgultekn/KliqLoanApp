package com.kliq.loanapp.core.common.text

/**
 * A presentation-agnostic text holder. [Resource] carries an Android string-resource id (resolved
 * in the UI layer) so pure modules can describe user-facing text without depending on Android.
 */
sealed interface UiText {
    data class Dynamic(val value: String) : UiText
    data class Resource(val resId: Int, val args: List<Any> = emptyList()) : UiText
    data object Empty : UiText

    companion object {
        fun of(value: String): UiText = Dynamic(value)
        fun res(resId: Int, vararg args: Any): UiText = Resource(resId, args.toList())
    }
}
