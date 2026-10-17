package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import java.io.File

@CommandLine.Command(
    name = "run",
    description = ["Build and run the project"]
)
class RunCommand : Runnable {
    @CommandLine.Option(names = ["-h", "--help"], help = true)
    var helpRequested: Boolean = false

    @CommandLine.Option(names = ["-t", "--target"], description = ["Run target: android (adb), jar"], defaultValue = "jar")
    var target: String = "jar"

    @CommandLine.Option(names = ["-d", "--device"], description = ["Android device serial (for adb)"])
    var deviceSerial: String? = null

    @CommandLine.Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    var verbose: Boolean = false

    override fun run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out)
            return
        }

        println("Running project (target=$target)...")

        when (target) {
            "jar" -> runJar()
            "android" -> runAndroid()
            else -> System.err.println("Unknown run target: $target (use 'jar' or 'android')")
        }
    }

    private fun runJar() {
        val jarFile = findOutputJar()
        if (jarFile == null) {
            System.err.println("No JAR found in build/outputs/. Run 'mgebt build -t jar' first.")
            return
        }

        println("  Executing: java -jar ${jarFile.name}")
        val pb = ProcessBuilder("java", "-jar", jarFile.absolutePath)
            .directory(jarFile.parentFile ?: File("."))
            .inheritIO()
        val process = pb.start()
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            System.err.println("Process exited with code $exitCode")
        }
    }

    private fun runAndroid() {
        val adbCmd = mutableListOf("adb")
        if (!deviceSerial.isNullOrBlank()) {
            adbCmd.add("-s")
            adbCmd.add(deviceSerial!!)
        }

        // check device
        val check = ProcessBuilder(adbCmd + "get-state").start()
        if (check.waitFor() != 0) {
            System.err.println("No Android device connected. Connect a device or use -d <serial>.")
            return
        }

        val apkFile = findOutputApk()
        if (apkFile == null) {
            System.err.println("No APK found in build/outputs/. Run 'mgebt build -t android' first.")
            return
        }

        println("  Installing ${apkFile.name}...")
        val install = ProcessBuilder(adbCmd + "install -r ${apkFile.absolutePath}").start()
        val installOut = install.inputStream.bufferedReader().readText()
        install.waitFor()
        print(installOut)

        val configFile = File("mgebt.toml")
        if (!configFile.exists()) {
            System.err.println("mgebt.toml not found.")
            return
        }

        val pkgName = extractPackageName(configFile)
        if (pkgName == null) {
            System.err.println("Cannot determine package name from mgebt.toml")
            return
        }

        println("  Launching $pkgName...")
        val launch = ProcessBuilder(adbCmd + "shell am start -n $pkgName/.MainActivity").start()
        launch.waitFor()

        if (verbose) {
            println("  --- logcat ---")
            ProcessBuilder(adbCmd + "logcat -s MonoGE:V").inheritIO().start().waitFor()
        }

        println("✓ App launched")
    }

    private fun findOutputJar(): File? {
        val outputs = File("build/outputs")
        if (!outputs.exists()) return null
        return outputs.walkTopDown().filter { it.extension == "jar" }.firstOrNull()
    }

    private fun findOutputApk(): File? {
        val outputs = File("build/outputs")
        if (!outputs.exists()) return null
        return outputs.walkTopDown().filter { it.extension == "apk" }.firstOrNull()
    }

    private fun extractPackageName(configFile: File): String? {
        for (line in configFile.readLines()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("package")) {
                val match = Regex("""package\s*=\s*"([^"]+)"""").find(trimmed)
                if (match != null) return match.groupValues[1]
            }
        }
        return null
    }
}
