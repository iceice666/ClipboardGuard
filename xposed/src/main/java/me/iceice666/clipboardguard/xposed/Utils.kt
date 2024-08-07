package me.iceice666.clipboardguard.xposed

import java.io.File

object Utils {

    fun ensureFile(file: File) {
        if (!file.exists()) file.createNewFile()
        else if (!file.isFile) {
            file.deleteRecursively()
            file.createNewFile()
        }

    }

    fun ensureFile(path: String) {
        ensureFile( File(path))
    }

    fun ensureFolder(file: File) {
        if (!file.exists()) file.mkdirs()
        else if (!file.isDirectory) {
            file.deleteRecursively()
            file.mkdirs()
        }

    }

    fun ensureFolder(path: String) {
        ensureFolder(File(path))
    }

}