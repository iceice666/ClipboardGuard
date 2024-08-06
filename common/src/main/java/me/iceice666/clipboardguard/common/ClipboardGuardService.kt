package me.iceice666.clipboardguard.common

import me.iceice666.clipboardguard.common.internal.FilterCounter
import me.iceice666.clipboardguard.common.internal.LogManager
import java.io.File

object ClipboardGuardService {
    private const val TAG = "ClipboardGuardService"
    private var instance: ClipboardGuardService? = null

    private const val ROOT_DIR: String = "/data/system/clipboard_guard"
    //  private var configFile: File = File("$ROOT_DIR/config.json")


    val filterCounter = FilterCounter(File("$ROOT_DIR/counter"))
    val logManager = LogManager(File("$ROOT_DIR/log"))


    init {
        setupEnvironment()
        instance = this
    }


    private fun setupEnvironment() {
        File("/data/misc/clipboard_guard").deleteRecursively()

        if (!File("$ROOT_DIR/installed").exists()) {
            if (File(ROOT_DIR).exists()) File(ROOT_DIR).deleteRecursively()
            File(ROOT_DIR).mkdirs()
            File("$ROOT_DIR/installed").createNewFile()
        }
    }

    fun getInstance() = instance

}