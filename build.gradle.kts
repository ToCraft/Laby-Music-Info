plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "dev.tocraft"
version = System.getenv().getOrDefault("VERSION", properties["version"] as String)

labyMod {
    defaultPackageName = "dev.tocraft.musicinfo"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    // When the property is set to true, you can log in with a Minecraft account
                    // devLogin = true
                }
            }
        }
    }

    addonInfo {
        namespace = "musicinfo"
        displayName = "Music Info"
        author = "To_Craft"
        description = "See what music you hear!"
        minecraftVersion = "*"
        version = rootProject.version.toString()
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version

    repositories {
        maven("https://libraries.minecraft.net/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://maven.resourcefulbees.com/repository/thatgravyboat/")
        maven("https://jitpack.io")
    }
}