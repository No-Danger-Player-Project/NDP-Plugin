plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-waterfall") version "2.3.1"
}

group = "top.alazeprt.ndpp"
version = "1.0"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
}

tasks {
    jar {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("NDP-BungeeCord-${project.version}.jar")
    }

    runWaterfall {
        waterfallVersion("1.21")
    }
}