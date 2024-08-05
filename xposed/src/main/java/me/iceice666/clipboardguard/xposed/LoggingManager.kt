package me.iceice666.clipboardguard.xposed

import android.util.Log
import me.iceice666.clipboardguard.common.RemotePreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object LoggingManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())

    private fun buildXposedLog(msg: MessagePacket): String {
        val level = msg.level.toString()
        val time = dateFormat.format(Calendar.getInstance())
        val packageName = msg.packageName
        val message = msg.message.apply { if (endsWith("\n")) substring(0, length - 1) }
        val cause = (Log.getStackTraceString(msg.cause)
            .apply { if (endsWith("\n")) substring(0, length - 1) })
            .run { if (isNotBlank()) "\nCaused by: $this" else "" }

        return "[$level][$time][$packageName]: $message$cause\n"
    }

    private fun buildInternalLog(msg: MessagePacket): String {
        val level = msg.level.toString()
        val packageName = msg.packageName
        val message = msg.message.apply { if (endsWith("\n")) substring(0, length - 1) }
        val cause = Log.getStackTraceString(msg.cause)
            .apply { if (endsWith("\n")) substring(0, length - 1) }

        return "$level$$$packageName$$$message$$$cause"
    }


    fun new(module: XposedEntryInstance): Logger {
        return Logger(module)
    }

    enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    data class MessagePacket(
        val packageName: String,
        val level: Level,
        val message: String,
        val cause: Throwable? = null,
    )

    class Logger(private val module: XposedEntryInstance) {
        private val prefKeyFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())

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
            cause: Throwable? = null,
            toXposedLog: Boolean = false
        ) {
            val msg = MessagePacket(module.packageName, level, message, cause)

            if (toXposedLog) {
                module.log(buildXposedLog(msg))
            }

            module.getRemotePreferences(RemotePreferences.LOGGING).edit().apply {
                putString(
                    prefKeyFormat.format(Calendar.getInstance()),
                    buildInternalLog(msg)
                )
                apply()
            }
        }
    }
}
