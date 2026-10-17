package com.monoge.mgebt.cli

import com.monoge.mgebt.cli.commands.InitCommand
import com.monoge.mgebt.cli.commands.ConfigureCommand
import com.monoge.mgebt.cli.commands.BuildCommand
import com.monoge.mgebt.cli.commands.CleanCommand
import com.monoge.mgebt.cli.commands.DaemonCommand
import com.monoge.mgebt.cli.commands.TaskCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "mgebt",
    description = ["MonoGE Build Toolkit - CLI build tool for MonoGE projects"],
    subcommands = [
        InitCommand::class,
        ConfigureCommand::class,
        BuildCommand::class,
        CleanCommand::class,
        DaemonCommand::class,
        TaskCommand::class
    ],
    mixinStandardHelpOptions = true,
    version = ["MGEBT 0.1.0-stage3"]
)
class CliParser : Runnable {
    override fun run() {
        CommandLine.usage(this, System.out)
    }
}
