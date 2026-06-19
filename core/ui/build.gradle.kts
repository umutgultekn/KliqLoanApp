plugins {
    id("kliq.android.library.compose")
}

android {
    namespace = "com.kliq.loanapp.core.ui"
}

dependencies {
    api(project(":core:designsystem"))
    api(project(":core:model"))
    api(project(":core:common"))

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.javax.inject)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
}
