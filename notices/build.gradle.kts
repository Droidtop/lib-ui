// The notice colours (README.md), plain Kotlin/JVM so they are tested with no device and no SDK. Products
// compile these sources into their own modules (droidtop: runtime-common's srcDir), pinned as a submodule.
plugins {
    kotlin("jvm")
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
    testImplementation("junit:junit:4.13.2")
}
