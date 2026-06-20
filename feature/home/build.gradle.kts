plugins {
    id("kliq.android.feature")
}

android {
    namespace = "com.kliq.loanapp.feature.home"
}

dependencies {
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
}
