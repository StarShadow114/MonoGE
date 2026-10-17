package com.monoge.mgebt.task

class TaskGraph {
    private val tasks = mutableMapOf<String, Task>()
    private val adjacency = mutableMapOf<String, MutableList<String>>()

    fun register(task: Task) {
        tasks[task.name] = task
        adjacency.putIfAbsent(task.name, mutableListOf())
        for (dep in task.dependencies) {
            adjacency[dep]?.add(task.name)
        }
    }

    fun execute(target: String? = null): Boolean {
        val order = topologicalSort()
        val toRun = if (target != null) {
            order.filter { it == target || isReachable(target, it) }
        } else {
            order
        }

        for (taskName in toRun) {
            val task = tasks[taskName] ?: continue
            println("[TASK] $taskName: ${task.description}")
            if (!task.execute()) {
                println("[FAILED] Task '$taskName' failed")
                return false
            }
        }
        return true
    }

    private fun topologicalSort(): List<String> {
        val visited = mutableSetOf<String>()
        val result = mutableListOf<String>()

        fun dfs(node: String) {
            if (!visited.contains(node)) {
                visited.add(node)
                adjacency[node]?.forEach { dfs(it) }
                result.add(node)
            }
        }

        tasks.keys.forEach { dfs(it) }
        return result
    }

    private fun isReachable(from: String, to: String): Boolean {
        val queue = ArrayDeque<String>()
        val visited = mutableSetOf<String>()
        queue.add(from)
        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            if (curr == to) return true
            if (!visited.add(curr)) continue
            adjacency[curr]?.forEach { queue.add(it) }
        }
        return false
    }
}
