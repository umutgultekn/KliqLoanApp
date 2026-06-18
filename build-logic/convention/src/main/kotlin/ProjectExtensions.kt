import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Accessor for the `libs` version catalog from inside convention plugins.
 * MUST be `internal` so it does not leak onto consumer build-script classpaths and
 * shadow Gradle's generated `libs` catalog accessor.
 */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
