plugins {
    id("kliq.android.application")
    id("kliq.android.hilt")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.kliq.loanapp"

    defaultConfig {
        applicationId = "com.kliq.loanapp"
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            // R8 on: shrink + obfuscate code and strip unused resources. Keep rules are narrow
            // (see proguard-rules.pro + data's consumer-rules.pro) so the seam is actually exercised.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:login"))
    implementation(project(":feature:portfolio"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.timber)
}
