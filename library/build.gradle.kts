import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kmpNativeCoroutines)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.l2hyunwoo.compose.camera"
version = "1.0.0-alpha01"

kotlin {
    androidLibrary {
        namespace = "io.github.l2hyunwoo.compose.camera"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kmp.nativecoroutines.annotations)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.androidx.lifecycle.runtime)

            // CameraX
            implementation(libs.camerax.core)
            implementation(libs.camerax.camera2)
            implementation(libs.camerax.lifecycle)
            implementation(libs.camerax.video)
            implementation(libs.camerax.compose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

// Maven publishing configuration
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "compose-camera", version.toString())

    pom {
        name = "Compose Camera"
        description = "A camera library for Compose Multiplatform supporting Android and iOS"
        inceptionYear = "2024"
        url = "https://github.com/l2hyunwoo/compose-camera/"
        licenses {
            license {
                name = "Apache License 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "l2hyunwoo"
                name = "Hyunwoo Lee"
                url = "https://github.com/l2hyunwoo"
            }
        }
        scm {
            url = "https://github.com/l2hyunwoo/compose-camera"
            connection = "scm:git:git://github.com/l2hyunwoo/compose-camera.git"
            developerConnection = "scm:git:ssh://git@github.com/l2hyunwoo/compose-camera.git"
        }
    }
}
