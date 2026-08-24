plugins {
    java
    id("com.gradleup.shadow") version "9.0.0"
}

group = "ru.mossheaven"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.21.11-R0.1-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("mCommand-${project.version}.jar")
    }
    build { dependsOn(shadowJar) }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
