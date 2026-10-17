package com.monoge.mgebt.cli.commands

import com.monoge.mgebt.util.Logger
import picocli.CommandLine

@CommandLine.Command(name = "task", description = ["Task management"])
class TaskCommand : Runnable {

    @CommandLine.Parameters(index = "0", arity = "0..1", description = ["Subcommand: list"])
    var subcommand: String? = null

    override fun run() {
        when (subcommand) {
            "list", null -> {
                Logger.info("Available tasks:")
                Logger.info("  deps:check       - Check engine dependencies")
                Logger.info("  deps:download    - Download engine so from CI")
                Logger.info("  template:prepare - Prepare build template")
                Logger.info("  compile:java     - Compile Java/Kotlin to dex")
                Logger.info("  merge:dex        - Merge dex files")
                Logger.info("  merge:resources  - Merge resources")
                Logger.info("  package          - Package APK")
                Logger.info("  sign             - Sign APK")
            }
            else -> Logger.error("Unknown subcommand: $subcommand")
        }
    }
}
