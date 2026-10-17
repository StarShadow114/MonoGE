package com.monoge.mgebt.util

import java.io.File
import java.security.MessageDigest

object FileUtils {

    fun sha256(file: File): String {
        if (!file.exists()) return ""
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun copyDirectory(src: File, dst: File) {
        if (!src.exists()) return
        dst.mkdirs()
        src.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                copyDirectory(file, File(dst, file.name))
            } else {
                file.copyTo(File(dst, file.name), overwrite = true)
            }
        }
    }

    fun findFiles(dir: File, extension: String): List<File> {
        val result = mutableListOf<File>()
        dir.walkTopDown().filter { it.extension == extension }.forEach { result.add(it) }
        return result
    }
}
