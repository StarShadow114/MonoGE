package com.monoge.mgebt.cli.commands

import com.monoge.mgebt.util.Logger
import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command(name = "build", description = ["Build the project"])
class BuildCommand : Runnable {

    @CommandLine.Option(names = ["--no-incremental"], description = ["Force full rebuild"])
    var noIncremental: Boolean = false

    @CommandLine.Option(names = ["--task", "-t"], description = ["Run specific task only"])
    var taskName: String? = null

    override fun run() {
        Logger.info("Build starting (incremental=${!noIncremental})")
        Logger.warn("Build not yet implemented (stage 3.3)")
        exitProcess(1)
    }
}
