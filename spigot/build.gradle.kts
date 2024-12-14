import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.goooler.shadow")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api(projects.spigotCommon.v121R3) {
        attributes {
            // hack - need to *include* the newer implementations
            // but still need this module to target the oldest java version (8)
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("com.google.inject", "dev.kejona.crossplatforms.shaded.guice")

            // older versions of Spigot have a Guava that is too old for Guice
            relocate("com.google.common", "dev.kejona.crossplatforms.shaded.google.common") // i.e. Guava

            relocate("cloud.commandframework", "dev.kejona.crossplatforms.shaded.cloud")
            relocate("me.lucko.commodore", "dev.kejona.crossplatforms.shaded.commodore")
            relocate("net.kyori", "dev.kejona.crossplatforms.shaded.kyori")
            relocate("org.spongepowered.configurate", "dev.kejona.crossplatforms.shaded.configurate")
            relocate("io.leangen.geantyref", "dev.kejona.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.kejona.crossplatforms.shaded.bstats")

            // Because of old spigot versions having ancient yaml and not having an NBT api
            relocate("org.yaml.snakeyaml", "dev.kejona.crossplatforms.shaded.snakeyaml")
            relocate("de.tr7zw.changeme.nbtapi", "dev.kejona.crossplatforms.shaded.nbtapi")
            relocate("de.tr7zw.annotations", "dev.kejona.crossplatforms.shaded.tr7zw.annotations")
        }
        exclude {
                e ->
            val name = e.name
            (name.startsWith("com.google")
                    && !name.startsWith("com.google.inject")
                    && !name.startsWith("com.google.guava")) // contains package name of com.google.common
        }
    }

    archiveFileName.set("CrossplatForms-Spigot.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "spigot"
