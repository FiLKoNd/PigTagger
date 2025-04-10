pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
    }
}

rootProject.name = "PigTagger"

val versions = listOf("1.16.5", "1.19.4", "1.20.4", "1.20.6", "1.21.1", "1.21.4")
include(versions)