package com.monoge.mgebt.config

import com.moandjiezana.toml.Toml
import java.io.File

data class ProjectConfig(
    val project: ProjectInfo,
    val build: BuildConfig,
    val android: AndroidConfig,
    val dependencies: DependenciesConfig
) {
    data class ProjectInfo(
        val name: String,
        val version: String,
        val type: String
    )

    data class BuildConfig(
        val templateCompile: Boolean,
        val templateSource: String,
        val incremental: Boolean,
        val daemon: Boolean
    )

    data class AndroidConfig(
        val minSdk: Int,
        val targetSdk: Int,
        val packageName: String
    )

    data class DependenciesConfig(
        val engineRepo: String,
        val engineArtifact: String,
        val engineArchitectures: List<String>
    )

    companion object {
        fun load(file: File): ProjectConfig {
            val toml = Toml().read(file)
            return ProjectConfig(
                project = ProjectInfo(
                    name = toml.getString("project.name") ?: "Untitled",
                    version = toml.getString("project.version") ?: "0.1.0",
                    type = toml.getString("project.type") ?: "android"
                ),
                build = BuildConfig(
                    templateCompile = toml.getBoolean("build.template_compile") ?: true,
                    templateSource = toml.getString("build.template_source") ?: "github",
                    incremental = toml.getBoolean("build.incremental") ?: true,
                    daemon = toml.getBoolean("build.daemon") ?: true
                ),
                android = AndroidConfig(
                    minSdk = toml.getLong("android.min_sdk")?.toInt() ?: 24,
                    targetSdk = toml.getLong("android.target_sdk")?.toInt() ?: 34,
                    packageName = toml.getString("android.package") ?: "com.starshadowstudio.game.noa"
                ),
                dependencies = DependenciesConfig(
                    engineRepo = toml.getString("dependencies.engine_repo") ?: "StarShadow114/MonoGE",
                    engineArtifact = toml.getString("dependencies.engine_artifact") ?: "libmonolith.so",
                    engineArchitectures = toml.getList("dependencies.engine_architectures")?.map { it.toString() }
                        ?: listOf("arm64-v8a", "armeabi-v7a")
                )
            )
        }
    }
}
