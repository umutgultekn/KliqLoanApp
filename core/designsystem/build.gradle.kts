plugins {
    id("kliq.android.library.compose")
}

android {
    namespace = "com.kliq.loanapp.core.designsystem"
}

dependencies {
    // Strict leaf: depends only on :core:common (semantic Tone, UiText, ValidationRule) + Compose.
    implementation(project(":core:common"))

    // androidx-compose-ui/runtime/foundation are provided by the kliq.android.library.compose convention.
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
}
