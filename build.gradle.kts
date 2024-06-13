import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.konan.properties.Properties

// Reference: https://github.com/Dr-TSNG/Hide-My-Applist/blob/master/build.gradle.kts#L13-L103

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
}


fun shellCmd(cmd: String, currentWorkingDir: File = file("./")): String {
    val byteOut = java.io.ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = cmd.split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

val appVersion by extra("0.1.0")
val applicationId by extra("net.iceice666.clipboardblocker")

val minSdkVer by extra(24)
val targetSdkVer by extra(34)
val buildToolsVer by extra("34.0.0")


val gitCommitCount = shellCmd("git rev-list HEAD --count").toInt()
val gitCommitShortHash = shellCmd("git rev-parse --verify --short HEAD")

val androidSourceCompatibility = JavaVersion.VERSION_17
val androidTargetCompatibility = JavaVersion.VERSION_17

val localProperties = Properties()
localProperties.load(file("local.properties").inputStream())

val isOfficialBuild by extra(localProperties.getProperty("officialBuild", "false") == "true")

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

fun Project.configureBaseExtension() {

    extensions.findByType<BaseExtension>()?.run {
        compileSdkVersion(targetSdkVer)
        buildToolsVersion = buildToolsVer

        defaultConfig {
            minSdk = minSdkVer
            targetSdk = targetSdkVer

            versionCode = gitCommitCount
            versionName = appVersion
            versionNameSuffix = "-$gitCommitShortHash"

            consumerProguardFiles("proguard-rules.pro")
        }

        // singing config
//        val sConfig = localProperties.getProperty("fileDir")?.let {
//            signingConfigs.create("releaseSigningConfig") {
//                storeFile = file(it)
//                storePassword = localProperties.getProperty("storePassword")
//                keyAlias = localProperties.getProperty("keyAlias")
//                keyPassword = localProperties.getProperty("keyPassword")
//            }
//        }

        buildTypes {
            all {
//                signingConfig =  sConfig ?: signingConfigs["debug"]
            }
            named("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }

        compileOptions {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }

    extensions.findByType<ApplicationExtension>()?.run {
        buildTypes {
            named("release") {
                isShrinkResources = true
            }
        }
    }
}

subprojects {
    plugins.withId("com.android.application") {
        configureBaseExtension()
    }
    plugins.withId("com.android.library") {
        configureBaseExtension()
    }
}