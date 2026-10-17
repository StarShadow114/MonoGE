package com.monoge.mgebt.util

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}

object Logger {

    var level: LogLevel = LogLevel.INFO

    fun debug(message: String) {
        if (level <= LogLevel.DEBUG) println("[DEBUG] $message")
    }

    fun info(message: String) {
        if (level <= LogLevel.INFO) println("[INFO] $message")
    }

    fun warn(message: String) {
        if (level <= LogLevel.WARN) println("[WARN] $message")
    }

    fun error(message: String) {
        if (level <= LogLevel.ERROR) System.err.println("[ERROR] $message")
    }
}
