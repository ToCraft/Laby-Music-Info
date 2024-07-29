rootProject.name = "labymod4-music-info"

pluginManagement {
    val labyGradlePluginVersion = "0.4.6"
    plugins {
        id("net.labymod.gradle") version (labyGradlePluginVersion)
    }

    buildscript {
        repositories {
            maven("https://dist.labymod.net/api/v1/maven/release/")
            maven("https://repo.spongepowered.org/repository/maven-public")
            mavenCentral()
            gradlePluginPortal()
        }

        dependencies {
            classpath("net.labymod.gradle", "addon", labyGradlePluginVersion)
            classpath("com.github.johnrengelman", "shadow", "8.1.1")
        }
    }
}

plugins.apply("net.labymod.gradle")

include(":api")
include(":core")
