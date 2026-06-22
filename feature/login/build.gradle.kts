plugins {
    id("kliq.android.feature")
}

android {
    namespace = "com.kliq.loanapp.feature.login"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

    // Compose UI tests run under Robolectric in the unit-test source set (no device needed).
    // The test manifest is a testImplementation (not debugImplementation) so the ComponentActivity
    // createComposeRule needs is present in BOTH the debug and release unit-test variants.
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
}
