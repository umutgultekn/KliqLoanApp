pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KliqLoanApp"

include(":app")
include(":core:model")
include(":core:common")
include(":domain")
include(":data")
include(":core:designsystem")
include(":core:ui")
include(":feature:login")
include(":feature:home")
include(":core:testing")
