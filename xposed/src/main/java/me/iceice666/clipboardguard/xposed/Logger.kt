package me.iceice666.clipboardguard.xposed

import me.iceice666.clipboardguard.common.datakind.MessagePacket

@Suppress("unused")
class Logger(
    private var tag: String,
    private var redirectMessage: (MessagePacket) -> Unit = { _ -> }
) {
    fun debug(message: String) =
        log(MessagePacket.Level.DEBUG, message, null)

    fun info(message: String) =
        log(MessagePacket.Level.INFO, message, null)

    fun warn(message: String, cause: Throwable? = null) =
        log(MessagePacket.Level.WARN, message, cause)

    fun error(message: String, cause: Throwable? = null) =
        log(MessagePacket.Level.ERROR, message, cause)

    fun fatal(message: String, cause: Throwable? = null) =
        log(MessagePacket.Level.FATAL, message, cause)

    private fun log(
        level: MessagePacket.Level,
        message: String,
        cause: Throwable? = null,
    ) {
        redirectMessage(MessagePacket(tag, level, message, cause))
    }
}