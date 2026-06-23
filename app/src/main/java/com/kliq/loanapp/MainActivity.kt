package com.kliq.loanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.loanapp.core.common.navigation.Navigator
import com.kliq.loanapp.core.designsystem.theme.KliqTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    // Same @Singleton instance AppViewModel routes through; injected here (not exposed off the VM) so
    // the NavHost can collect its commands.
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KliqTheme {
                val startState by appViewModel.startState.collectAsStateWithLifecycle()
                when (val state = startState) {
                    StartState.Loading -> LoadingScreen()
                    is StartState.Ready -> KliqApp(
                        navigator = navigator,
                        startLoggedIn = state.loggedIn,
                    )
                }
            }
        }
    }
}
