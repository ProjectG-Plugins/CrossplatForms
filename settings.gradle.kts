enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CrossplatForms"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("net.kyori.indra.git") version "3.1.3" // getting branch and commit info at compile time
        id("net.kyori.indra") version "3.0.1" // multi-release jar for referencing Java 16 geyser api
        id("io.github.goooler.shadow") version "8.1.7"// shadowing dependencies
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://repo.opencollab.dev/main/") // geyser etc
        maven("https://jitpack.io") // fixes issue with Cloudburst Protocol that geyser depends on
        maven("https://libraries.minecraft.net/") // brigadier

        maven("https://oss.sonatype.org/content/repositories/snapshots") // bungeecord, spigot
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // spigot
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") // PlaceholderAPI
        maven("https://repo.codemc.io//repository/maven-public/") // NBT api

        maven("https://mvn.exceptionflug.de/repository/exceptionflug-public/") // protocolize for proxies
        maven("https://repo.papermc.io/repository/maven-public/") // velocity
    }
}

include(":core")
include(":access-item")

include(":proxy")
include(":bungeecord")
include(":velocity")

include(":spigot")

include(":spigot-common:common")
findProject(":spigot-common:common")?.name = "common"

include(":spigot-common:v1_8_R3")
findProject(":spigot-common:v1_8_R3")?.name = "v1_8_R3"

include(":spigot-common:v1_9_R2")
findProject(":spigot-common:v1_9_R2")?.name = "v1_9_R2"

include(":spigot-common:v1_12_R1")
findProject(":spigot-common:v1_12_R1")?.name = "v1_12_R1"

include(":spigot-common:v1_13_R2")
findProject(":spigot-common:v1_13_R2")?.name = "v1_13_R2"

include(":spigot-common:v1_14_R1")
findProject(":spigot-common:v1_14_R1")?.name = "v1_14_R1"

include(":spigot-common:v1_20_R2")
findProject(":spigot-common:v1_20_R2")?.name = "v1_20_R2"

include(":spigot-common:v1_21_R3")
findProject(":spigot-common:v1_21_R3")?.name = "v1_21_R3"
