package com.monoge.mgebt.config

import org.tomlj.Toml
import org.tomlj.TomlArray
import java.io.File

data class ProjectConfig(
    val projectName: String,
    val version: String,
    val engineVersion: String,
    val engineArchitectures: List<String>,
    val outputDir: String,
    val sourceDir: String,
    val assetsDir: String
) {
    companion object {
        fun load(configFile: File): ProjectConfig {
            val toml = Toml.parse(configFile.toPath())

            val projectName = toml.getString("project.name")
                ?: throw IllegalArgumentException("Missing project.name in config")
            val version = toml.getString("project.version") ?: "0.1.0"
            val engineVersion = toml.getString("engine.version") ?: "0.1.0"

            // tomlj uses getArray(), returns TomlArray
            val engineArchitectures = (toml.getArray("dependencies.engine_architectures"))
                ?.toList()
                ?.map { it.toString() }
                ?: listOf("arm64-v8a")

            val outputDir = toml.getString("build.output_dir") ?: "build"
            val sourceDir = toml.getString("build.source_dir") ?: "src"
            val assetsDir = toml.getString("build.assets_dir") ?: "assets"

            return ProjectConfig(
                projectName = projectName,
                version = version,
                engineVersion = engineVersion,
                engineArchitectures = engineArchitectures,
                outputDir = outputDir,
                sourceDir = sourceDir,
                assetsDir = assetsDir
            )
        }
    }
}
