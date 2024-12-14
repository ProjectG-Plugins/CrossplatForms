import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.goooler.shadow")
}

dependencies {
    //annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    api("cloud.commandframework:cloud-velocity:1.8.3")
    api("org.bstats:bstats-velocity:3.0.2")
    api(projects.proxy)
    api(projects.core)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // velocity's minimum
    }
}

tasks.withType<ShadowJar> {
    dependencies {
        shadow {
            relocate("cloud.commandframework", "dev.kejona.crossplatforms.shaded.cloud")
            relocate("org.spongepowered.configurate", "dev.kejona.crossplatforms.shaded.configurate")
            // Used by cloud and configurate
            relocate("io.leangen.geantyref", "dev.kejona.crossplatforms.shaded.typetoken")
            relocate("org.bstats", "dev.kejona.crossplatforms.shaded.bstats")
        }
        exclude {
                e -> e.name.startsWith("com.mojang") // all available on velocity
                || e.name.startsWith("org.yaml")
                || e.name.startsWith("com.google")
                || e.name.startsWith("net.kyori")
        }
    }

    archiveFileName.set("CrossplatForms-Velocity.jar")
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}

description = "velocity"
