package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File

@Command(
    name = "init",
    description = ["Initialize a new MonoGE project"]
)
class InitCommand : Runnable {

    @Option(names = ["-t", "--type"], description = ["Project type: android, jar"], defaultValue = "android")
    var type: String = "android"

    @Option(names = ["-n", "--name"], description = ["Project name"])
    var name: String? = null

    @Option(names = ["-s", "--sample"], description = ["Sample project directory"])
    var sample: String? = null

    override fun run() {
        val projectName = name ?: "my-monge-game"
        val projectDir = File(projectName)

        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }

        // Create project_config.toml
        val configFile = File(projectDir, "project_config.toml")
        configFile.writeText("""
            [project]
            name = "$projectName"
            type = "$type"
            version = "0.1.0"

            [dependencies]
            engine_architectures = ["arm64-v8a", "armeabi-v7a"]

            [build]
            output_dir = "build/out"
        """.trimIndent())

        // Create directory structure
        File(projectDir, "src/android").mkdirs()
        File(projectDir, "src/jar").mkdirs()
        File(projectDir, "src/lua").mkdirs()
        File(projectDir, "lib").mkdirs()
        File(projectDir, "build").mkdirs()

        println("✓ Initialized MonoGE project: $projectName (type: $type)")
    }
}
