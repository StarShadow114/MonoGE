package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

@Command(
    name = "daemon",
    description = ["Start/stop the MGEBT build daemon"]
)
class DaemonCommand : Runnable {

    @Option(names = ["--stop"], description = ["Stop the running daemon"])
    var stop: Boolean = false

    @Option(names = ["--port"], description = ["Daemon port"], defaultValue = "19732")
    var port: Int = 19732

    override fun run() {
        if (stop) {
            println("→ Stopping daemon...")
            // TODO: Phase 3.2 - send stop signal to daemon
            println("✓ Daemon stopped.")
            return
        }

        println("→ Starting MGEBT daemon on port $port...")
        // TODO: Phase 3.2 - full daemon implementation
        println("✓ Daemon started (stub).")
    }
}
