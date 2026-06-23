package com.kliq.loanapp.core.common.text

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * A presentation-agnostic text holder. [Resource] carries an Android string-resource id (resolved
 * in the UI layer) so pure modules can describe user-facing text without depending on Android. Args
 * are [ImmutableList] so the holders stay genuinely stable when carried inside @Immutable UI configs.
 */
sealed interface UiText {
    data class Dynamic(val value: String) : UiText
    data class Resource(val resId: Int, val args: ImmutableList<Any> = persistentListOf()) : UiText
    data class Plural(val resId: Int, val quantity: Int, val args: ImmutableList<Any> = persistentListOf()) : UiText
    data object Empty : UiText

    companion object {
        fun of(value: String): UiText = Dynamic(value)
        fun res(resId: Int, vararg args: Any): UiText = Resource(resId, args.toList().toImmutableList())
        fun plural(resId: Int, quantity: Int, vararg args: Any): UiText =
            Plural(resId, quantity, args.toList().toImmutableList())
    }
}
