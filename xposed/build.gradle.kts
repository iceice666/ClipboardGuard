plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "net.iceice666.clipboardblocker.xposed"

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    compileOnly(libs.de.robv.android.xposed.api)


    implementation(project(":common"))
}

kotlin {
    jvmToolchain(17)
}