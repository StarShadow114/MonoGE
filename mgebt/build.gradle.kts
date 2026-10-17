plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.monoge"
version = "0.1.0-stage3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("info.picocli:picocli:4.7.5")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.monoge.mgebt.MainKt")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "com.monoge.mgebt.MainKt",
            "Implementation-Version" to project.version
        )
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}
