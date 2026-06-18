plugins {
    id("kliq.jvm.library")
}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    api(project(":domain"))

    api(libs.junit4)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    implementation(libs.kotlinx.coroutines.core)
}
