plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "top.alazeprt.ndpp"
version = "1.0"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks {
    jar {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("NDP-Velocity-${project.version}.jar")
    }
}