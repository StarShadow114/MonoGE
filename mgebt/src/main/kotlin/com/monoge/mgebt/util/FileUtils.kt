package com.monoge.mgebt.util

import java.io.File
import java.security.MessageDigest

object FileUtils {
    fun sha256(file: File): String {
        if (!file.exists()) return ""
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun sha256OfFiles(paths: List<String>): String {
        val digest = MessageDigest.getInstance("SHA-256")
        for (path in paths.sorted()) {
            val file = File(path)
            if (file.exists()) {
                digest.update(path.toByteArray())
                digest.update(sha256(file).toByteArray())
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun copyDir(src: File, dest: File) {
        if (!dest.exists()) dest.mkdirs()
        src.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                copyDir(file, File(dest, file.name))
            } else {
                file.copyTo(File(dest, file.name), overwrite = true)
            }
        }
    }
}
