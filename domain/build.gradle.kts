plugins {
    id("kliq.jvm.library")
}

dependencies {
    api(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)

    // Domain main needs no core:common (only a KDoc reference); the AppError taxonomy is used solely
    // by the tests, so it stays a test-only dependency — keeping the domain's production surface lean.
    testImplementation(project(":core:common"))
    testImplementation(project(":core:testing"))
}
