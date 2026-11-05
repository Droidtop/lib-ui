// The palette a theme resolves to (ThemePalette, @Serializable as droidtop stores it), the focus ring's inks and the
// notice colours of a palette. Plain Kotlin/JVM, tested with no device and no SDK.
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":notices"))
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}
