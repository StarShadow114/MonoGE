package com.monoge.mgebt

import com.monoge.mgebt.cli.CliParser
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val exitCode = CliParser.parse(args)
    exitProcess(exitCode)
}
