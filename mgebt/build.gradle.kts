plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.monoge"
version = "0.1.0-stage3"

repositories {
    maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") }
    maven { url = uri("https://mirrors.cloud.tencent.com/maven-central/") }
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("info.picocli:picocli:4.7.5")
    implementation("org.tomlj:tomlj:1.1.1")
}

application {
    mainClass.set("com.monoge.mgebt.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.monoge.mgebt.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<JavaExec>("run-mgebt") {
    dependsOn("jar")
    classpath = files(tasks.jar.get().archiveFile)
    mainClass.set("com.monoge.mgebt.MainKt")
    // 透传命令行参数：./gradlew run --args="init -n test"
    if (project.hasProperty("args")) {
        args = (project.property("args") as String).split(" ")
    }
}