package com.monoge.mgebt.config

import com.monoge.mgebt.util.FileUtils
import org.tomlj.Toml
import java.io.File

data class ProjectConfig(
    val name: String,
    val type: String,
    val version: String,
    val engineArchitectures: List<String>,
    val outputDir: String
) {
    companion object {
        fun load(configFile: File = File("project_config.toml")): ProjectConfig {
            if (!configFile.exists()) {
                throw IllegalArgumentException("project_config.toml not found in ${configFile.absolutePath}")
            }

            val toml = Toml.parse(configFile.readText())

            if (toml.hasErrors()) {
                throw IllegalArgumentException("Invalid TOML: ${toml.errors()}")
            }

            val projectTable = toml.getTable("project")
                ?: throw IllegalArgumentException("Missing [project] section")

            val name = projectTable.getString("name") ?: "unnamed"
            val type = projectTable.getString("type") ?: "android"
            val version = projectTable.getString("version") ?: "0.1.0"

            val engineArchitectures = try {
                @Suppress("UNCHECKED_CAST")
                (toml.getList("dependencies.engine_architectures") as? List<Any?>)
                    ?.map { it.toString() }
                    ?: listOf("arm64-v8a", "armeabi-v7a")
            } catch (e: Exception) {
                listOf("arm64-v8a", "armeabi-v7a")
            }

            val outputDir = toml.getTable("build")?.getString("output_dir") ?: "build/out"

            return ProjectConfig(name, type, version, engineArchitectures, outputDir)
        }
    }
}
