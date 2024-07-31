package me.iceice666.clipboardguard.xposed

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Custom logging module
class Logger(
    private var name: String,
    private var xposedLogger: ((String) -> Unit)? = null
) {


    fun debug(message: String) {
        log(Level.DEBUG, message)
    }

    fun info(message: String) {
        log(Level.INFO, message)
    }

    fun warn(message: String) {
        log(Level.WARN, message)
    }

    fun error(message: String) {
        log(Level.ERROR, message)
    }

    fun fatal(message: String) {
        log(Level.FATAL, message)
    }

    private fun log(level: Level, message: String, cause: Throwable? = null) {
        var text = ""

        text += when (level) {
            Level.DEBUG -> "[Debug]"
            Level.INFO -> "[Info]"
            Level.WARN -> "[Warn]"
            Level.ERROR -> "[Error]"
            Level.FATAL -> "[Fatal]"
        }
        text += "[${SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}]"
        text += "[$name]"
        text += message
        if (!text.endsWith("\n")) text += "\n"
        if (cause != null) text += Log.getStackTraceString(cause)
        if (!text.endsWith("\n")) text += "\n"

        xposedLogger?.let {
            it(text)
        }

    }


    enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }
}