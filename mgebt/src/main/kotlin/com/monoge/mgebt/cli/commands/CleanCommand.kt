package com.monoge.mgebt.cli.commands

import com.monoge.mgebt.util.Logger
import picocli.CommandLine
import java.io.File

@CommandLine.Command(name = "clean", description = ["Clean build outputs"])
class CleanCommand : Runnable {

    override fun run() {
        val buildDir = File("build")
        if (buildDir.exists()) {
            buildDir.deleteRecursively()
            Logger.success("Cleaned build/")
        } else {
            Logger.info("Nothing to clean")
        }
    }
}
