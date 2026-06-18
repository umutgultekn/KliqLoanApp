import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.kliq.loanapp.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "kliq.android.application"
            implementationClass = "KliqAndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "kliq.android.library"
            implementationClass = "KliqAndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "kliq.android.library.compose"
            implementationClass = "KliqAndroidLibraryComposeConventionPlugin"
        }
        register("jvmLibrary") {
            id = "kliq.jvm.library"
            implementationClass = "KliqJvmLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "kliq.android.hilt"
            implementationClass = "KliqAndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "kliq.android.feature"
            implementationClass = "KliqAndroidFeatureConventionPlugin"
        }
    }
}
