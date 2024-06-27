
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:6.0.54") // see https://www.nathaan.com/explorer/?package=com.mojang&name=authlib
    api(projects.spigotCommon.v114R1)
}
