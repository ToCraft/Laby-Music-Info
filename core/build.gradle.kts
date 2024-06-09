version = "0.1.0"

plugins {
    id("java-library")
}

dependencies {
    api(project(":api"))

    maven(mavenCentral(), "org.java-websocket:Java-WebSocket:1.5.6")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}