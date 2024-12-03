version = rootProject.version

plugins {
    id("com.github.johnrengelman.shadow")
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    api(project(":api"))

    maven(mavenCentral(), "org.java-websocket:Java-WebSocket:1.5.7")
    maven(mavenCentral(), "org.jellyfin.sdk:jellyfin-core:1.6.3")

    // shade in case it vanishes
    shade("tech.thatgravyboat:jukebox-jvm:1.0-SNAPSHOT")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    shadowJar {
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}