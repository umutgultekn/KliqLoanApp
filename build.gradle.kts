import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

// Top-level build file. Standard plugins are declared `apply false` so their classpath is available
// to the convention plugins in `:build-logic`, which apply them by id per module.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.detekt) apply false
}

// Captured at root scope — the type-safe `libs` accessor is not in scope inside `subprojects {}`.
val detektFormatting = libs.detekt.formatting
val detektConfig = files("config/detekt/detekt.yml")

// Static analysis (detekt + ktlint via detekt-formatting) for every module.
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        autoCorrect = true
        parallel = true
        config.setFrom(detektConfig)
    }

    dependencies {
        add("detektPlugins", detektFormatting)
    }

    tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
            sarif.required.set(false)
            md.required.set(false)
        }
    }
}
