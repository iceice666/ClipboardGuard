package me.iceice666.clipboardguard.common.internal

import me.iceice666.clipboardguard.common.Utils.ensureFile
import me.iceice666.clipboardguard.common.Utils.ensureFolder
import me.iceice666.clipboardguard.common.datakind.MessagePacket
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class LogManager(private val rootFolder: File) {

    private val runtimeLog = File(rootFolder, "runtime.log")
    private var latestLog: File? = null

    private val fileLock = Any()

     val logCache = mutableListOf<MessagePacket>()

    init {
        ensureFolder(rootFolder)
        ensureFile(runtimeLog)

        runtimeLog.writeText("")
    }

    fun writeLog(msg: MessagePacket) = synchronized(fileLock) {
        if (latestLog == null || latestLog!!.length() > 1024 * 1024) {
            newLog()
        }

        val log = msg.toString()

        logCache.add(msg)

        latestLog!!.appendText(log)
        runtimeLog.appendText(log)
    }

    private fun newLog() = synchronized(fileLock) {
        val filename = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Calendar.getInstance())

        latestLog = File(rootFolder, "$filename.log")
        ensureFile(latestLog!!)
    }

    fun getLog() = synchronized(fileLock) {
        runtimeLog.readText()
    }

}