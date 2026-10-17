package com.monoge.mgebt.cli

import com.monoge.mgebt.cli.commands.*
import picocli.CommandLine

@CommandLine.Command(
    name = "mgebt",
    description = ["MonoGE Build Toolkit - CLI build tool for MonoGE projects"],
    version = ["MGEBT 0.1.0-stage3"],
    mixinStandardHelpOptions = true,
    subcommands = [
        InitCommand::class,
        ConfigureCommand::class,
        BuildCommand::class,
        CleanCommand::class,
        DaemonCommand::class,
        TaskCommand::class,
        RunCommand::class
    ]
)
class CliParser : Runnable {
    override fun run() {
        CommandLine.usage(CliParser(), System.out)
    }
}
