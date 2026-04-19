pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "jpatcher"

include("core")
include("impl223")
include("impl243")
include("jpatcher-plugin")
include("jpatcher-plugin-mcp")