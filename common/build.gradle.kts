plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.buildconfig)
}

val applicationId: String by rootProject.extra

android {
    namespace = "net.iceice666.clipboardguard.common"

    buildFeatures {
        buildConfig = true
    }

    buildConfig {
        packageName("net.iceice666.clipboardguard.common")
        useKotlinOutput {
            topLevelConstants = true
            internalVisibility = false
        }

        buildConfigField("APP_NAME", rootProject.name)
        buildConfigField("PACKAGE_ID", applicationId)

    }


}
kotlin {
    jvmToolchain(17)
}
dependencies {


}