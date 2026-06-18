package com.kliq.loanapp.core.designsystem.text

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
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
        stringResource(resId, *args.toTypedArray())
    }
    UiText.Empty -> ""
}

/** Non-composable resolver for use outside composition (e.g. building a snackbar message). */
fun UiText.asString(context: Context): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> if (args.isEmpty()) {
        context.getString(resId)
    } else {
        context.getString(resId, *args.toTypedArray())
    }
    UiText.Empty -> ""
}
