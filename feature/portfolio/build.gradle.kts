plugins {
    id("kliq.android.feature")
}

android {
    namespace = "com.kliq.loanapp.feature.portfolio"
}

dependencies {
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
