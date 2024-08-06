plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "me.iceice666.clipboardguard"

    buildFeatures {
        buildConfig = true
        viewBinding = true

        compose = true

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    defaultConfig {
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")




    implementation(libs.libxposed.service)

    implementation(project(":common"))
    runtimeOnly(project(":xposed"))
}

kotlin {
    jvmToolchain(17)
}