package com.monoge.mgebt.cli

import picocli.CommandLine

object CliParser {
    private val commandLine = CommandLine(RootCommand()).apply {
        addSubcommand("init", InitCommand())
        addSubcommand("configure", ConfigureCommand())
        addSubcommand("build", BuildCommand())
        addSubcommand("clean", CleanCommand())
        addSubcommand("daemon", DaemonCommand())
        addSubcommand("task", TaskCommand())
    }

    fun parse(args: Array<String>): Int {
        return try {
            commandLine.execute(*args)
        } catch (e: Exception) {
            System.err.println("Error: ${e.message}")
            1
        }
    }
}

@CommandLine.Command(
    name = "mgebt",
    version = ["MGEBT 0.1.0-stage3"],
    description = ["MonoGE Build Toolkit"],
    mixinStandardHelpOptions = true
)
class RootCommand : Runnable {
    override fun run() {
        CommandLine.usage(this, System.out)
    }
}
