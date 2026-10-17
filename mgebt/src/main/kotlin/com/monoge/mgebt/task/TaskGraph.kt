package com.monoge.mgebt.task

class TaskGraph {

    private val tasks = mutableMapOf<String, Task>()

    fun register(task: Task) {
        tasks[task.name] = task
    }

    fun get(name: String): Task? = tasks[name]

    fun execute(name: String) {
        val task = tasks[name] ?: throw IllegalArgumentException("Task '$name' not found")
        executeWithDeps(task)
    }

    private fun executeWithDeps(task: Task) {
        for (dep in task.dependsOn) {
            val depTask = tasks[dep] ?: throw IllegalArgumentException("Dependency '$dep' not found")
            executeWithDeps(depTask)
        }
        println("→ Executing task: ${task.name}")
        task.execute()
    }

    fun listTasks() {
        for (task in tasks.values) {
            println("  ${task.name} - ${task.description}")
        }
    }
}
