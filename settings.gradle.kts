pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
    }
}

rootProject.name = "PigTagger"

include("1.20.4")