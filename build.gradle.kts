plugins {
    kotlin("jvm") version "2.2.21" apply false
}

subprojects {
    group = "dev.droidtop.lib.ui"
    version = rootProject.property("lib.version") as String
}
