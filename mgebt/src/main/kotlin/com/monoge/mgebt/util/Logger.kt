package com.monoge.mgebt.util

object Logger {
    private const val RESET  = "\u001B[0m"
    private const val RED    = "\u001B[31m"
    private const val GREEN  = "\u001B[32m"
    private const val YELLOW = "\u001B[33m"
    private const val BLUE   = "\u001B[34m"
    private const val BOLD   = "\u001B[1m"

    fun info(msg: String) {
        println("${BLUE}[INFO]${RESET} $msg")
    }

    fun success(msg: String) {
        println("${GREEN}[OK]${RESET} $msg")
    }

    fun warn(msg: String) {
        println("${YELLOW}[WARN]${RESET} $msg")
    }

    fun error(msg: String) {
        println("${RED}[ERROR]${RESET} $msg")
    }

    fun task(name: String, msg: String) {
        println("${BOLD}[$name]${RESET} $msg")
    }
}
