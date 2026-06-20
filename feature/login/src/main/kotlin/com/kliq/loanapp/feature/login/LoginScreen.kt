package com.kliq.loanapp.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kliq.loanapp.core.common.navigation.KliqRoute
import com.kliq.loanapp.core.designsystem.component.ButtonSize
import com.kliq.loanapp.core.designsystem.component.EmailFormField
import com.kliq.loanapp.core.designsystem.component.KliqLoadingOverlay
import com.kliq.loanapp.core.designsystem.component.KliqScaffold
import com.kliq.loanapp.core.designsystem.component.KliqText
import com.kliq.loanapp.core.designsystem.component.KliqTextStyle
import com.kliq.loanapp.core.designsystem.component.PasswordFormField
import com.kliq.loanapp.core.designsystem.component.PrimaryButton
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import com.kliq.loanapp.core.ui.rememberSnackbarEvents

/** Registers the login destination in the app's navigation graph. */
fun NavGraphBuilder.loginScreen() {
    composable<KliqRoute.Login> { LoginRoute() }
}

@Composable
fun LoginRoute(viewModel: LoginViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = rememberSnackbarEvents(viewModel.events)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        loading = isLoading,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onEmailFocusChanged = viewModel::onEmailFocusChanged,
        onPasswordFocusChanged = viewModel::onPasswordFocusChanged,
        onEmailImeNext = viewModel::onEmailImeNext,
        onSubmit = viewModel::onSubmit,
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    snackbarHostState: SnackbarHostState,
    loading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEmailFocusChanged: (Boolean) -> Unit,
    onPasswordFocusChanged: (Boolean) -> Unit,
    onEmailImeNext: () -> Unit,
    onSubmit: () -> Unit,
) {
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val submit = {
        focusManager.clearFocus()
        onSubmit()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KliqScaffold(snackbarHostState = snackbarHostState) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = KliqTheme.spacing.xxxl),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.kliq_logo),
                    contentDescription = stringResource(R.string.login_logo_description),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(KliqTheme.sizes.logoHeight),
                )
                Spacer(Modifier.height(KliqTheme.spacing.xl))
                KliqText(text = stringResource(R.string.login_subtitle), style = KliqTextStyle.Body, color = KliqTheme.colors.textSecondary)
                Spacer(Modifier.height(KliqTheme.spacing.huge))

                EmailFormField(
                    value = state.email,
                    state = state.emailState,
                    onValueChange = onEmailChange,
                    onImeAction = {
                        onEmailImeNext()
                        passwordFocusRequester.requestFocus()
                    },
                    onFocusChanged = onEmailFocusChanged,
                )
                Spacer(Modifier.height(KliqTheme.spacing.xl))
                PasswordFormField(
                    value = state.password,
                    state = state.passwordState,
                    onValueChange = onPasswordChange,
                    onImeAction = submit,
                    focusRequester = passwordFocusRequester,
                    onFocusChanged = onPasswordFocusChanged,
                    imeAction = ImeAction.Done,
                )
                Spacer(Modifier.height(KliqTheme.spacing.xxxl))
                PrimaryButton(
                    text = stringResource(R.string.login_submit),
                    onClick = submit,
                    size = ButtonSize.Large,
                    fullWidth = true,
                    enabled = state.submitEnabled,
                )
            }
        }
        // Blocking overlay for the security-sensitive sign-in (banking pattern), instead of a
        // button spinner. Driven by the shared isLoading flag.
        KliqLoadingOverlay(visible = loading, message = stringResource(R.string.login_loading))
    }
}
