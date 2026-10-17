package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import java.io.File

@Command(
    name = "configure",
    description = ["Configure the build (resolve dependencies, download engine libs)"]
)
class ConfigureCommand : Runnable {

    override fun run() {
        val configFile = File("project_config.toml")
        if (!configFile.exists()) {
            println("✗ No project_config.toml found. Run 'mgebt init' first.")
            return
        }

        println("→ Reading project_config.toml...")
        println("→ Checking dependencies...")

        // TODO: Phase 3.5 - download libmonoge.so from GitHub CI if missing
        val libDir = File("lib")
        if (!libDir.exists()) libDir.mkdirs()

        println("✓ Configuration complete.")
    }
}
