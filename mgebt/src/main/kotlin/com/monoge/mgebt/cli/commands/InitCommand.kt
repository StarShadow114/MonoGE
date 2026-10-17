package com.monoge.mgebt.cli.commands

import com.monoge.mgebt.util.Logger
import picocli.CommandLine
import java.io.File

@CommandLine.Command(name = "init", description = ["Initialize a new MonoGE project"])
class InitCommand : Runnable {

    @CommandLine.Option(names = ["--type"], defaultValue = "android", description = ["Project type: android, jar"])
    lateinit var type: String

    @CommandLine.Option(names = ["--name"], description = ["Project name"])
    var name: String? = null

    @CommandLine.Option(names = ["--sample"], description = ["Include sample code"])
    var sample: Boolean = false

    @CommandLine.Parameters(index = "0", description = ["Project directory"], arity = "0..1")
    var dir: String? = null

    override fun run() {
        val projectDir = File(dir ?: ".")
        val projectName = name ?: projectDir.name

        Logger.info("Initializing project '$projectName' (type: $type)")

        projectDir.mkdirs()
        File(projectDir, "src").mkdirs()
        File(projectDir, "lib/arm64-v8a").mkdirs()
        File(projectDir, "lib/armeabi-v7a").mkdirs()
        File(projectDir, "build/out").mkdirs()
        File(projectDir, "assets").mkdirs()

        val config = """
            [project]
            name = "$projectName"
            version = "0.1.0"
            type = "$type"

            [build]
            template_compile = true
            template_source = "github"
            incremental = true
            daemon = true

            [android]
            min_sdk = 24
            target_sdk = 34
            package = "com.starshadowstudio.game.noa"

            [dependencies]
            engine_repo = "StarShadow114/MonoGE"
            engine_artifact = "libmonolith.so"
            engine_architectures = ["arm64-v8a", "armeabi-v7a"]
        """.trimIndent()
        File(projectDir, "project_config.toml").writeText(config)

        val gitignore = """
            build/
            .mgebt-cache/
            *.apk
            *.jar
        """.trimIndent()
        File(projectDir, ".gitignore").writeText(gitignore)

        if (sample) {
            File(projectDir, "assets/main.lua").writeText("""
                print("Hello from NoA!")
            """.trimIndent())
        }

        Logger.success("Project initialized at ${projectDir.absolutePath}")
        Logger.info("Run 'mgebt configure' then 'mgebt build' to build.")
    }
}
