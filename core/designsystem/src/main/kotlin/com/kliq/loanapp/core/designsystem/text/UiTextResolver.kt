package com.kliq.loanapp.core.designsystem.text

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.kliq.loanapp.core.common.text.UiText

/** Resolves a [UiText] to a displayable string within composition (the only Android-aware step). */
@Composable
@ReadOnlyComposable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> if (args.isEmpty()) {
        stringResource(resId)
    } else {
        stringResource(resId, *args.resolve())
    }
    is UiText.Plural -> pluralStringResource(resId, quantity, *args.resolve())
    UiText.Empty -> ""
}

/** Non-composable resolver for use outside composition (e.g. building a snackbar message). */
fun UiText.asString(context: Context): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> if (args.isEmpty()) {
        context.getString(resId)
    } else {
        context.getString(resId, *args.resolve(context))
    }
    is UiText.Plural -> context.resources.getQuantityString(resId, quantity, *args.resolve(context))
    UiText.Empty -> ""
}

/** Resolves any nested [UiText] args first so a resource can take a localized string as an argument. */
@Composable
@ReadOnlyComposable
private fun List<Any>.resolve(): Array<Any> =
    map { if (it is UiText) it.asString() else it }.toTypedArray()

private fun List<Any>.resolve(context: Context): Array<Any> =
    map { if (it is UiText) it.asString(context) else it }.toTypedArray()
