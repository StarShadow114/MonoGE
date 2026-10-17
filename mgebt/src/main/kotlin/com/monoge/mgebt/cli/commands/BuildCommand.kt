package com.monoge.mgebt.cli.commands

import picocli.CommandLine

@CommandLine.Command(
    name = "build",
    description = ["Build the project (APK or JAR)"]
)
class BuildCommand : Runnable {
    @CommandLine.Option(names = ["-h", "--help"], help = true)
    var helpRequested: Boolean = false

    @CommandLine.Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    var verbose: Boolean = false

    @CommandLine.Option(names = ["-t", "--target"], description = ["Build target: android, jar, all"], defaultValue = "all")
    var target: String = "all"

    @CommandLine.Option(names = ["-o", "--output"], description = ["Output directory"])
    var outputDir: String? = null

    override fun run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out)
            return
        }

        println("Building project (target=$target, verbose=$verbose)...")
        if (outputDir != null) println("  output=$outputDir")
        println("  (build logic will be implemented in stage 3.3)")
    }
}
