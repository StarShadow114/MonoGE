package com.monoge.mgebt

import com.monoge.mgebt.cli.CliParser
import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val cmd = CliParser()
    val exitCode = CommandLine(cmd).execute(*args)
    exitProcess(exitCode)
}
