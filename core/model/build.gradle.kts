plugins {
    id("kliq.jvm.library")
}

dependencies {
    // Pure Kotlin domain model — no Android, no runtime third-party deps.
    testImplementation(libs.junit4)
}
