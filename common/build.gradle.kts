import org.jetbrains.kotlin.cli.jvm.main

plugins {

    kotlin("plugin.serialization") version "2.0.0"

    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")

}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}

val applicationId: String by rootProject.extra

fun String.toQuotedString(): String {
    return "\"$this\""
}

android {
    namespace = "me.iceice666.clipboardguard.common"

    buildFeatures {
        buildConfig = true
    
    }

    defaultConfig {


        buildConfigField("String", "APP_NAME", rootProject.name.toQuotedString())
        buildConfigField("String", "PACKAGE_ID", applicationId.toQuotedString())


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