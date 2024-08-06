package me.iceice666.clipboardguard.common

import me.iceice666.clipboardguard.common.datakind.MessagePacket


@Suppress("unused")
class Logger(
    private val identifier: String,
    private var redirectMessage: (String) -> Unit = { _ -> }
) {
    fun setupRedirect(redirect: (String) -> Unit) {
        this.redirectMessage = redirect
    }

    fun debug(message: String, redirect: Boolean = false) =
        log(MessagePacket.Level.DEBUG, message, null, redirect)

    fun info(message: String, redirect: Boolean = false) =
        log(MessagePacket.Level.INFO, message, null, redirect)

    fun warn(message: String, cause: Throwable? = null, redirect: Boolean = false) =
        log(MessagePacket.Level.WARN, message, cause, redirect)

    fun error(message: String, cause: Throwable? = null, redirect: Boolean = true) =
        log(MessagePacket.Level.ERROR, message, cause, redirect)

    fun fatal(message: String, cause: Throwable? = null, redirect: Boolean = true) =
        log(MessagePacket.Level.FATAL, message, cause, redirect)

    private fun log(
        level: MessagePacket.Level,
        message: String,
        cause: Throwable? = null,
        redirect: Boolean = false
    ) {
        val msg = MessagePacket(identifier, level, message, cause)

        ClipboardGuardService.getInstance()?.logManager?.writeLog(msg)

        if (redirect) redirectMessage(msg.toString())


    }

}