package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import java.io.File

@Command(
    name = "clean",
    description = ["Clean build outputs"]
)
class CleanCommand : Runnable {

    override fun run() {
        val buildDir = File("build")
        if (buildDir.exists()) {
            buildDir.deleteRecursively()
            println("✓ Cleaned build/ directory.")
        } else {
            println("Nothing to clean.")
        }
    }
}
