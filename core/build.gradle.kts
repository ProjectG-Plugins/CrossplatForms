
repositories {
    maven("https://repo.opencollab.dev/main/")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("org.geysermc.cumulus:cumulus:1.0-SNAPSHOT") // needed for testing button components
    testImplementation("com.google.code.gson:gson:2.8.6") // needed for cumulus

    compileOnly("org.geysermc:geyser-api:2.0.2-SNAPSHOT")
    compileOnly("org.geysermc:core:2.0.2-SNAPSHOT") {
        isTransitive = false // exclude all the junk we won't and can't use
    }
    compileOnly("org.geysermc.floodgate:api:2.1.0-SNAPSHOT")
    api("cloud.commandframework:cloud-core:1.6.2")
    api("cloud.commandframework:cloud-minecraft-extras:1.6.2")
    api("net.kyori:adventure-api:4.10.0")
    api("net.kyori:adventure-text-serializer-legacy:4.10.0")
    api("org.spongepowered:configurate-yaml:4.1.2")
}

description = "core"
