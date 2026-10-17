package com.monoge.mgebt.task

interface Task {
    val name: String
    val description: String
    val dependencies: List<String>

    fun inputs(): List<String>
    fun outputs(): List<String>
    fun fingerprint(): String
    fun execute(): Boolean
}
