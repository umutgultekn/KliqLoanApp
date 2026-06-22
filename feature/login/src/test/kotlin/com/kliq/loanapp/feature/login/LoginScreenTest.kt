package com.kliq.loanapp.feature.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Compose UI test for the Login screen, run under Robolectric (no device). Verifies the screen
 * renders its state correctly and is interactive — the test-pyramid layer the JVM ViewModel tests
 * cannot reach.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class LoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun setScreen(
        state: LoginUiState,
        loading: Boolean = false,
        onSubmit: () -> Unit = {},
    ) {
        composeRule.setContent {
            KliqTheme {
                LoginScreen(
                    state = state,
                    snackbarHostState = SnackbarHostState(),
                    loading = loading,
                    onEmailChange = {},
                    onPasswordChange = {},
                    onEmailFocusChanged = {},
                    onPasswordFocusChanged = {},
                    onEmailImeNext = {},
                    onSubmit = onSubmit,
                )
            }
        }
    }

    @Test
    fun `clicking Sign In with filled fields invokes onSubmit`() {
        var submitted = false
        setScreen(LoginUiState(email = "user@kliq.com", password = "secret1"), onSubmit = { submitted = true })
        composeRule.onNodeWithText("Sign In").performClick()
        assertTrue(submitted)
    }

    @Test
    fun `Sign In is disabled when fields are empty`() {
        setScreen(LoginUiState())
        composeRule.onNodeWithText("Sign In").assertIsNotEnabled()
    }

    @Test
    fun `loading shows the blocking overlay`() {
        setScreen(LoginUiState(email = "user@kliq.com", password = "secret1"), loading = true)
        composeRule.onNodeWithContentDescription("Signing in…").assertIsDisplayed()
    }
}
