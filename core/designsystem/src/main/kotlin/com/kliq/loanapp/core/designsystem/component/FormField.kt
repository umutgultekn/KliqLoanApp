package com.kliq.loanapp.core.designsystem.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kliq.loanapp.core.common.text.UiText
import com.kliq.loanapp.core.designsystem.R
import com.kliq.loanapp.core.designsystem.text.asString
import com.kliq.loanapp.core.designsystem.theme.KliqTheme

@Immutable
data class TextConfig(
    val label: UiText,
    val placeholder: UiText = UiText.Empty,
    val supporting: UiText? = null,
)

@Immutable
data class KeyboardConfig(
    val keyboardType: KeyboardType = KeyboardType.Text,
    val imeAction: ImeAction = ImeAction.Next,
    val isSecret: Boolean = false,
)

@Immutable
data class FormFieldConfig(
    val text: TextConfig,
    val keyboard: KeyboardConfig = KeyboardConfig(),
)

/** Hoisted visual state of a field. Validation is computed by the ViewModel and rendered here. */
@Immutable
sealed interface FieldUiState {
    data object Idle : FieldUiState
    data object Focused : FieldUiState
    data object Valid : FieldUiState
    data class Error(val message: UiText) : FieldUiState
}

/**
 * Config-driven, fully-hoisted form field: label + input + validation feedback + focus wiring. The
 * field is stateless; validation runs in the ViewModel and is fed back as a [FieldUiState].
 */
@Composable
fun FormField(
    value: String,
    state: FieldUiState,
    config: FormFieldConfig,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFocusChanged: (Boolean) -> Unit = {},
    trailing: @Composable (() -> Unit)? = null,
) {
    val colors = KliqTheme.colors
    val errorMessage = (state as? FieldUiState.Error)?.message
    val errorText = errorMessage?.asString()
    val borderColor = when (state) {
        is FieldUiState.Error -> colors.statusDefault
        FieldUiState.Focused, FieldUiState.Valid -> colors.statusActive
        FieldUiState.Idle -> colors.border
    }

    Column(modifier = modifier.fillMaxWidth().animateContentSize()) {
        KliqText(
            text = config.text.label.asString(),
            style = KliqTextStyle.Caption,
            modifier = Modifier.padding(bottom = KliqTheme.spacing.sm),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .semantics { if (errorText != null) error(errorText) },
            singleLine = true,
            isError = state is FieldUiState.Error,
            shape = KliqTheme.shapes.field,
            visualTransformation = if (config.keyboard.isSecret) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = config.keyboard.keyboardType,
                imeAction = config.keyboard.imeAction,
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() },
            ),
            placeholder = { KliqText(config.text.placeholder.asString(), style = KliqTextStyle.Body, color = colors.textSecondary) },
            trailingIcon = trailing,
            // Solid surface fill lifts the field off the muted background.
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                errorBorderColor = colors.statusDefault,
                focusedContainerColor = colors.surface,
                unfocusedContainerColor = colors.surface,
                errorContainerColor = colors.surface,
            ),
        )
        val supporting = errorMessage ?: config.text.supporting
        if (supporting != null && supporting != UiText.Empty) {
            KliqText(
                text = supporting.asString(),
                style = KliqTextStyle.Caption,
                color = if (errorMessage != null) colors.statusDefault else colors.textSecondary,
                modifier = Modifier.padding(top = KliqTheme.spacing.xs),
            )
        }
    }
}

@Composable
fun EmailFormField(
    value: String,
    state: FieldUiState,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFocusChanged: (Boolean) -> Unit = {},
    label: UiText = UiText.res(R.string.kliq_field_email_label),
    placeholder: UiText = UiText.res(R.string.kliq_field_email_placeholder),
) {
    FormField(
        value = value,
        state = state,
        config = FormFieldConfig(
            text = TextConfig(label = label, placeholder = placeholder),
            keyboard = KeyboardConfig(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        ),
        onValueChange = onValueChange,
        onImeAction = onImeAction,
        modifier = modifier,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
    )
}

@Composable
fun PasswordFormField(
    value: String,
    state: FieldUiState,
    onValueChange: (String) -> Unit,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onFocusChanged: (Boolean) -> Unit = {},
    imeAction: ImeAction = ImeAction.Done,
    label: UiText = UiText.res(R.string.kliq_field_password_label),
    placeholder: UiText = UiText.res(R.string.kliq_field_password_placeholder),
) {
    var visible by remember { mutableStateOf(false) }
    FormField(
        value = value,
        state = state,
        config = FormFieldConfig(
            text = TextConfig(label = label, placeholder = placeholder),
            keyboard = KeyboardConfig(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
                isSecret = !visible,
            ),
        ),
        onValueChange = onValueChange,
        onImeAction = onImeAction,
        modifier = modifier,
        focusRequester = focusRequester,
        onFocusChanged = onFocusChanged,
        trailing = {
            KliqIconButton(
                icon = if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = stringResource(
                    if (visible) R.string.kliq_action_hide_password else R.string.kliq_action_show_password,
                ),
                onClick = { visible = !visible },
                stateDescription = stringResource(
                    if (visible) R.string.kliq_password_shown else R.string.kliq_password_hidden,
                ),
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun FormFieldPreview() {
    KliqTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            EmailFormField(value = "a@b.com", state = FieldUiState.Valid, onValueChange = {}, onImeAction = {})
            FormField(
                value = "123",
                state = FieldUiState.Error(UiText.of("Password too short")),
                config = FormFieldConfig(TextConfig(UiText.of("Password"))),
                onValueChange = {},
                onImeAction = {},
            )
        }
    }
}
