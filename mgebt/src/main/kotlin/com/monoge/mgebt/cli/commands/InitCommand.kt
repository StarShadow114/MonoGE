package com.monoge.mgebt.cli.commands

import picocli.CommandLine
import java.io.File

@CommandLine.Command(
    name = "init",
    description = ["Initialize a new MonoGE project"]
)
class InitCommand : Runnable {
    @CommandLine.Option(names = ["-h", "--help"], help = true)
    var helpRequested: Boolean = false

    @CommandLine.Option(names = ["-n", "--name"], description = ["Project name"])
    var projectName: String? = null

    @CommandLine.Option(names = ["-t", "--type"], description = ["Project type: android, jar"], defaultValue = "android")
    var projectType: String = "android"

    @CommandLine.Option(names = ["-s", "--sample"], description = ["Sample project directory"])
    var sampleDir: String? = null

    @CommandLine.Option(names = ["-d", "--dir"], description = ["Target directory (default: current dir)"], defaultValue = ".")
    var targetDir: String = "."

    override fun run() {
        if (helpRequested) {
            CommandLine.usage(this, System.out)
            return
        }

        val target = File(targetDir).absoluteFile
        if (!target.exists()) {
            target.mkdirs()
        }

        val name = (projectName ?: target.name).trim()
        if (name.isBlank()) {
            System.err.println("Error: Project name cannot be empty. Use -n <name>")
            return
        }

        println("Initializing MonoGE project '$name' (type: $projectType)...")

        generateProjectStructure(target)
        generateConfig(target, name)
        generateSourceTemplate(target, name, projectType)
        generateBuildGradle(target, name, projectType)
        generateSettingsGradle(target, name)

        println("✓ Project '$name' initialized at ${target.path}")
        println("  Next steps:")
        println("    cd ${target.path}")
        println("    mgebt configure")
        println("    mgebt build")
    }

    private fun generateProjectStructure(target: File) {
        val dirs = listOf(
            "src/main/kotlin",
            "src/main/resources",
            "src/android/res/values",
            "src/android/res/layout",
            "src/android/assets",
            "src/android/jniLibs/arm64-v8a",
            "build/outputs",
            "libs"
        )
        for (d in dirs) {
            File(target, d).mkdirs()
        }
    }

    private fun generateConfig(target: File, name: String) {
        val config = """
            [project]
            name = "$name"
            version = "0.1.0"
            type = "$projectType"

            [engine]
            version = "0.1.0"
            min_sdk = 24
            target_sdk = 34

            [dependencies]
            engine_architectures = ["arm64-v8a"]

            [build]
            output_dir = "build/outputs"
            source_dir = "src"
            assets_dir = "src/android/assets"
            jni_libs_dir = "src/android/jniLibs"
        """.trimIndent()
        File(target, "mgebt.toml").writeText(config)
        println("  ✓ mgebt.toml")
    }

    private fun generateSourceTemplate(target: File, name: String, type: String) {
        val safePkg = "com.monoge." + name.lowercase().replace("-", "_").replace(" ", "")
        val pkgPath = safePkg.replace(".", "/")

        if (type == "android") {
            File(target, "src/main/kotlin/$pkgPath").mkdirs()

            val mainActivity = """
                package $safePkg

                import android.app.Activity
                import android.os.Bundle

                class MainActivity : Activity() {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        setContentView(R.layout.activity_main)
                    }
                }
            """.trimIndent()
            File(target, "src/main/kotlin/$pkgPath/MainActivity.kt").writeText(mainActivity)

            val layout = """
                <?xml version="1.0" encoding="utf-8"?>
                <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:orientation="vertical"
                    android:gravity="center">
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Hello MonoGE!"
                        android:textSize="24sp" />
                </LinearLayout>
            """.trimIndent()
            File(target, "src/android/res/layout/activity_main.xml").writeText(layout)

            val manifest = """
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    package="$safePkg">
                    <application
                        android:label="$name"
                        android:icon="@mipmap/ic_launcher">
                        <activity
                            android:name=".MainActivity"
                            android:exported="true">
                            <intent-filter>
                                <action android:name="android.intent.action.MAIN" />
                                <category android:name="android.intent.category.LAUNCHER" />
                            </intent-filter>
                        </activity>
                    </application>
                </manifest>
            """.trimIndent()
            File(target, "src/android/AndroidManifest.xml").writeText(manifest)

            println("  ✓ Android source template ($safePkg)")
        } else {
            File(target, "src/main/kotlin/$pkgPath").mkdirs()

            val mainKt = """
                package $safePkg

                fun main() {
                    println("Hello from $name!")
                }
            """.trimIndent()
            File(target, "src/main/kotlin/$pkgPath/Main.kt").writeText(mainKt)
            println("  ✓ JAR source template ($safePkg)")
        }
    }

    private fun generateBuildGradle(target: File, name: String, type: String) {
        val safePkg = "com.monoge." + name.lowercase().replace("-", "_").replace(" ", "")
        val buildGradle = if (type == "android") {
            """
                plugins {
                    id("com.android.application")
                    kotlin("android")
                }

                android {
                    namespace = "$safePkg"
                    compileSdk = 34

                    defaultConfig {
                        applicationId = "$safePkg"
                        minSdk = 24
                        targetSdk = 34
                    }

                    buildTypes {
                        release {
                            isMinifyEnabled = false
                        }
                    }
                }

                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
                    implementation(files("libs/mongengine.aar"))
                }
            """.trimIndent()
        } else {
            """
                plugins {
                    kotlin("jvm") version "1.9.22"
                }

                repositories {
                    mavenCentral()
                }

                dependencies {
                    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
                }

                tasks.jar {
                    manifest {
                        attributes["Main-Class"] = "$safePkg.MainKt"
                    }
                    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                }
            """.trimIndent()
        }
        File(target, "build.gradle.kts").writeText(buildGradle)
        println("  ✓ build.gradle.kts")
    }

    private fun generateSettingsGradle(target: File, name: String) {
        File(target, "settings.gradle.kts").writeText("rootProject.name = \"$name\"\n")
        println("  ✓ settings.gradle.kts")
    }
}
