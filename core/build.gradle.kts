import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

plugins {
    id("com.github.johnrengelman.shadow")
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    labyProcessor()
    api(project(":api"))

    addonMavenDependency("org.java-websocket:Java-WebSocket:1.5.7")
    addonMavenDependency("org.jellyfin.sdk:jellyfin-core:1.6.3")

    // shade in case it vanishes
    shade("tech.thatgravyboat:jukebox-jvm:1.0-SNAPSHOT")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
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