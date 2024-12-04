rootProject.name = "labymod4-music-info"

pluginManagement {
    val labyGradlePluginVersion = "0.5.7"
    buildscript {
        repositories {
            maven("https://dist.labymod.net/api/v1/maven/release/")
            maven("https://maven.neoforged.net/releases/")
            maven("https://maven.fabricmc.net/")
            gradlePluginPortal()
            mavenCentral()
            maven("https://repo.spongepowered.org/repository/maven-public")
        }

        dependencies {
            classpath("net.labymod.gradle", "common", labyGradlePluginVersion)
            classpath("com.github.johnrengelman", "shadow", "8.1.1")
        }
    }
}

plugins.apply("net.labymod.labygradle.settings")

include(":api")
include(":core")