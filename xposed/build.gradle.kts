plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "net.iceice666.clipboardblocker.xposed"

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

    compileOnly(libs.de.robv.android.xposed.api)


    implementation(libs.androidx.core.ktx)
    implementation(project(":common"))
}

kotlin {
    jvmToolchain(17)
}