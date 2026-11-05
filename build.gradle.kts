plugins {
    kotlin("jvm") version "2.2.21" apply false
    kotlin("plugin.serialization") version "2.2.21" apply false
    // Every module's public API is a checked-in dump (<module>/api/<module>.api); apiCheck fails on any difference
    // (Droidtop/tracker#867: stable library interfaces). scripts/check-api-compat.sh adds the version rule.
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.2"
}

subprojects {
    group = "dev.droidtop.lib.ui"
    version = rootProject.property("lib.version") as String
}
