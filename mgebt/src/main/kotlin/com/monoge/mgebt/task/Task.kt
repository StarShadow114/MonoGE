package com.monoge.mgebt.task

import java.io.File

abstract class Task(val name: String) {

    open val description: String = ""
    open val dependsOn: List<String> = emptyList()

    abstract fun execute()

    open fun shouldRun(inputFiles: List<File>, outputFiles: List<File>): Boolean {
        // TODO: Phase 3.3 - incremental fingerprint check
        return true
    }

    override fun toString(): String = "Task(name='$name')"
}
