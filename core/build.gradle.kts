
plugins {
    id("net.kyori.indra")
    id("java-test-fixtures") // used for the testFixtures source set, which is available for other test sources
}

sourceSets {
    main {
        multirelease {
            alternateVersions(16)
        }
    }

    create("java16")
}

// Add more configurations here as necessary
val java16Implementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    testImplementation("org.geysermc.cumulus:cumulus:1.1.1-SNAPSHOT") // needed for testing button components
    testImplementation("com.google.code.gson:gson:2.8.6") // needed for cumulus

    // Nullability annotations. todo: move to something else
    testImplementation("com.google.code.findbugs:jsr305:3.0.2")
    api("com.google.code.findbugs:jsr305:3.0.2")

    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT") { isTransitive = false }
    compileOnly("net.luckperms:api:5.4") { isTransitive = false }

    api("org.bstats:bstats-base:3.0.2")
    api("cloud.commandframework:cloud-core:1.8.3")
    api("cloud.commandframework:cloud-minecraft-extras:1.8.3")
    api("net.kyori:adventure-api:4.17.0")
    api("net.kyori:adventure-text-serializer-legacy:4.13.1")
    api("net.kyori:adventure-text-serializer-gson:4.17.0") {
        // This is required or else it overrides the version we explicitly define below
        exclude(group = "com.google.code.gson", module = "gson")
    }

    api("org.yaml:snakeyaml:1.26") // Version provided by velocity - shaded/relocated on Spigot and BungeeCord
    api("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    api("org.spongepowered:configurate-extra-guice:4.2.0-SNAPSHOT")

    api("com.google.inject:guice:5.1.0") {
       exclude(group = "com.google.guava", module = "guava") // Provides a newer version than provided by server platforms
    }

    // Provided by Velocity. A slightly higher version is provided by BungeeCord.
    // Shaded on Spigot because on older Spigot versions, the Guava is too old and breaks Guice.
    api("com.google.guava:guava") {
        version {
            prefer("30.1-jre")
        }
    }
    api("com.google.code.gson:gson") {
        version {
            prefer("2.3.1") // lowest version, provided by spigot 1.8.8
        }
    }

    // dependencies for java16 sources (optionally used at runtime)
    // if someone wants geyser to be directly used then they must be running java 16 or higher
    java16Implementation("org.geysermc.api:geyser-api:1.0.1-SNAPSHOT")
}

description = "core"
