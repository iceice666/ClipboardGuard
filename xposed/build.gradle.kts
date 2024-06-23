plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "net.iceice666.clipboardguard.xposed"

    buildFeatures {
        buildConfig = false
    }

    packaging {
        resources {
            merges += "META-INF/xposed/*"
            excludes += "**"
        }
    }
}

dependencies {
    compileOnly(libs.libxposed.api)



    implementation(project(":common"))
}

kotlin {
    jvmToolchain(17)
}