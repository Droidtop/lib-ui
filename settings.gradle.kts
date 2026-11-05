rootProject.name = "lib-ui"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// The backend contract every UI speaks (contract/README.md); plain JVM.
include(":contract")
project(":contract").projectDir = file("contract/kotlin")

// The notice colours shared by droidtop and Enginehost (Droidtop/notices, moved here with its history).
include(":notices")

// The palette a theme resolves to and the maths over it, shared by every UI (Droidtop/tracker#867). Plain JVM.
include(":tokens")
