import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class KliqAndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("kliq.android.library")
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        val extension = extensions.getByType<LibraryExtension>()
        configureAndroidCompose(extension)
    }
}
