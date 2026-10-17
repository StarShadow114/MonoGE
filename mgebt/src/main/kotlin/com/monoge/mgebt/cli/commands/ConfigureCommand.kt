package com.monoge.mgebt.cli.commands

import com.monoge.mgebt.config.ProjectConfig
import com.monoge.mgebt.util.Logger
import picocli.CommandLine

@CommandLine.Command(name = "configure", description = ["Read project config and prepare build graph"])
class ConfigureCommand : Runnable {

    override fun run() {
        val configFile = java.io.File("project_config.toml")
        if (!configFile.exists()) {
            Logger.error("project_config.toml not found. Run 'mgebt init' first.")
            return
        }

        val config = ProjectConfig.load(configFile)
        Logger.info("Project: ${config.project.name} v${config.project.version}")
        Logger.info("Type: ${config.project.type}")
        Logger.success("Configure complete")
    }
}
