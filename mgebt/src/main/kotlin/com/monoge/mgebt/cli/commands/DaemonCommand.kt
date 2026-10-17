package com.monoge.mgebt.cli.commands

import com.monoge.mgebt.util.Logger
import picocli.CommandLine

@CommandLine.Command(name = "daemon", description = ["Manage MGEBT daemon"])
class DaemonCommand : Runnable {

    @CommandLine.Parameters(index = "0", description = ["Action: start, stop, status"])
    lateinit var action: String

    override fun run() {
        when (action) {
            "start" -> Logger.info("Daemon start not yet implemented (stage 3.2)")
            "stop" -> Logger.info("Daemon stop not yet implemented (stage 3.2)")
            "status" -> Logger.info("Daemon status not yet implemented (stage 3.2)")
            else -> Logger.error("Unknown action: $action")
        }
    }
}
