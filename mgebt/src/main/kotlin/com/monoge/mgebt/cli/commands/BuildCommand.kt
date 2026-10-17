package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File

@Command(
    name = "build",
    description = ["Build the project (APK or JAR)"]
)
class BuildCommand : Runnable {

    @Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    var verbose: Boolean = false

    override fun run() {
        println("→ Starting build...")

        val configFile = File("project_config.toml")
        if (!configFile.exists()) {
            println("✗ No project_config.toml found. Run 'mgebt init' first.")
            return
        }

        // TODO: Phase 3.4 - full Android build pipeline
        println("→ Reading configuration...")
        println("→ Compiling sources...")
        println("→ Packaging output...")

        val outDir = File("build/out")
        if (!outDir.exists()) outDir.mkdirs()

        println("✓ Build complete. Output in build/out/")
    }
}
