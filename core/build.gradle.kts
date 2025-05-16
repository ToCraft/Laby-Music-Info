import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    labyProcessor()
    api(project(":api"))

    addonMavenDependency("org.java-websocket:Java-WebSocket:1.6.0")
    addonMavenDependency("org.jellyfin.sdk:jellyfin-core:1.6.8")
    addonMavenDependency("tech.thatgravyboat:jukebox-jvm:1.1")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}


java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
