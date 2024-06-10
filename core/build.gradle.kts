version = "0.1.0"

plugins {
    id("java-library")
}

dependencies {
    api(project(":api"))

    maven("https://maven.resourcefulbees.com/repository/thatgravyboat", "tech.thatgravyboat:jukebox-jvm:1.0-SNAPSHOT")
    maven("https://jitpack.io/", "com.github.LabyStudio:java-spotify-api:1.2.0")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}