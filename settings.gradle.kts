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
