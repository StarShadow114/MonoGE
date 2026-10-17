package com.monoge.mgebt.cli.commands

import picocli.CommandLine

@CommandLine.Command(
    name = "task",
    description = ["Run a specific build task"]
)
class TaskCommand : Runnable {
    @CommandLine.Option(names = ["-h", "--help"], help = true)
    var helpRequested: Boolean = false

    @CommandLine.Parameters(index = "0", description = ["Task name to run"])
    lateinit var taskName: String

    override fun run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out)
            return
        }

        println("Running task: $taskName")
        println("  (task execution engine will be implemented in stage 3.3)")
    }
}
