plugins {
    id("kliq.jvm.library")
}

dependencies {
    // Pure Kotlin domain model — no Android, no third-party deps.
    testImplementation(libs.junit4)
}
