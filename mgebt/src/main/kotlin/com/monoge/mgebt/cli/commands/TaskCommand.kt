package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(
    name = "task",
    description = ["Run a specific build task"]
)
class TaskCommand : Runnable {

    @Parameters(index = "0", description = ["Task name to run"])
    var taskName: String? = null

    override fun run() {
        if (taskName == null) {
            println("✗ No task specified. Usage: mgebt task <task-name>")
            return
        }

        println("→ Running task: $taskName")
        // TODO: Phase 3.3 - task system + incremental fingerprint
        println("✓ Task '$taskName' complete (stub).")
    }
}
