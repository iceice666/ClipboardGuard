package me.iceice666.clipboardguard.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessagePacket(
    val identifier: String,
    val level: Level,
    val message: String,
    val cause: Throwable? = null,
    val time: Calendar = Calendar.getInstance(),
) : Parcelable{
    enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    }

    override fun toString(): String {
        val time = SimpleDateFormat(
            "yyyy-MM-dd_HH:mm:ss",
            Locale.getDefault()
        ).format(time)
        val message = message.apply { if (endsWith("\n")) substring(0, length - 1) }
        val cause = cause?.let {
            (it.stackTraceToString()
                .apply { if (endsWith("\n")) substring(0, length - 1) })
                .run { if (isNotBlank()) "\nCaused by:\n$this" else "" }
        }

        return "[$level][$time][$identifier]: $message$cause\n"
    }
}