plugins {
    id("kliq.android.library")
    id("kliq.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kliq.loanapp.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":domain"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
}
