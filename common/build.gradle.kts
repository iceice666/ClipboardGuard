import org.jetbrains.kotlin.cli.jvm.main

plugins {

    kotlin("plugin.serialization") version "2.0.0"

    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.buildconfig)
    id("kotlin-parcelize")

}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

val applicationId: String by rootProject.extra

android {
    namespace = "me.iceice666.clipboardguard.common"

    buildFeatures {
        buildConfig = true
        aidl = true
    }

    buildConfig {
        packageName("me.iceice666.clipboardguard.common")
        useJavaOutput()

        buildConfigField("APP_NAME", rootProject.name)
        buildConfigField("PACKAGE_ID", applicationId)

    }

    packaging {
        resources {
            merges += "META-INF/xposed/*"
            excludes += "**"
        }
    }

    sourceSets {
        aidlPackagedList("src/main/aidl")
    }
}

kotlin {
    jvmToolchain(17)
}