plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.run-velocity") version "2.3.1"
    id("xyz.jpenilla.run-waterfall") version "2.3.1"
    id("com.gradleup.shadow") version "8.3.5"
}

group = "top.alazeprt.ndpp"
version = "1.0"

tasks.register("moveArtifacts") {
    subprojects.forEach { subproject ->
        if (subproject.name == "spigot" || subproject.name == "bungeecord" || subproject.name == "velocity") {
            copy {
                from (subproject.tasks.shadowJar.get().archiveFile)
                into ("dist")
            }
        }
    }
}

tasks {
    build {
        finalizedBy("moveArtifacts")
    }
}