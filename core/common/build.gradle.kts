plugins {
    id("kliq.jvm.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)
    implementation(libs.javax.inject)

    testImplementation(libs.junit4)
}
