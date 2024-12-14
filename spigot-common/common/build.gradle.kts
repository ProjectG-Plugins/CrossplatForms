
dependencies {
    testImplementation(testFixtures(projects.core))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.5.21") // Mirrored from floodgate-spigot - probably the version from 1.8.8
    compileOnly("me.clip:placeholderapi:2.11.6")
    api(projects.core)
    api(projects.accessItem)
    api("cloud.commandframework:cloud-paper:1.8.3")
    api("me.lucko:commodore:2.2")
    api("net.kyori:adventure-platform-bukkit:4.3.0")
    api("org.bstats:bstats-bukkit:3.0.2")
}

description = "spigot-common"
