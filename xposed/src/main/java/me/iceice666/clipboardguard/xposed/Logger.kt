package me.iceice666.clipboardguard.xposed

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Custom logging module
class Logger(
    private val module: XposedEntryInstance
) {


    fun debug(message: String, toXposedLog: Boolean = false) =
        log(Level.DEBUG, message, null, toXposedLog)

    fun info(message: String, toXposedLog: Boolean = false) =
        log(Level.INFO, message, null, toXposedLog)

    fun warn(message: String, cause: Throwable? = null, toXposedLog: Boolean = false) =
        log(Level.WARN, message, cause, toXposedLog)

    fun error(message: String, cause: Throwable? = null, toXposedLog: Boolean = true) =
        log(Level.ERROR, message, cause, toXposedLog)

    fun fatal(message: String, cause: Throwable? = null, toXposedLog: Boolean = true) =
        log(Level.FATAL, message, cause, toXposedLog)

    private fun log(
        level: Level,
        message: String,
        cause: Throwable?,
        toXposedLog: Boolean = false
    ) {
        val msg = generateMessage(level, message, cause)
        if (toXposedLog) module.log(msg)
    }

    private fun generateMessage(level: Level, message: String, cause: Throwable?): String {
        var text = ""

        text += when (level) {
            Level.DEBUG -> "[Debug]"
            Level.INFO -> "[Info]"
            Level.WARN -> "[Warn]"
            Level.ERROR -> "[Error]"
            Level.FATAL -> "[Fatal]"
        }
        text += "[${SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}]"
        text += "[${module.packageName}]"
        text += message
        if (!text.endsWith("\n")) text += "\n"
        if (cause != null) text += Log.getStackTraceString(cause)
        if (!text.endsWith("\n")) text += "\n"



        return text
    }


    enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
}