version = rootProject.version

plugins {
    id("java-library")
}

dependencies {
    labyApi("api")
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.INTERFACE
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}