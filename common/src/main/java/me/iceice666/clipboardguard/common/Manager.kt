package me.iceice666.clipboardguard.common

import me.iceice666.clipboardguard.common.datakind.ActionKind
import me.iceice666.clipboardguard.common.datakind.ContentType
import me.iceice666.clipboardguard.common.datakind.FieldSelector
import me.iceice666.clipboardguard.common.datakind.MessagePacket
import me.iceice666.clipboardguard.common.datakind.RuleSets

object Manager {
     val logCache = mutableListOf<MessagePacket>()
    var ruleSets = RuleSets().apply {
        update(FieldSelector("com.discord", ActionKind.Write, ContentType.Text)) {
            it.data.add(Regex(".*"))
        }
    }
    private val filterCounter = mutableMapOf<FieldSelector, Int>()

    fun getLogger(tag: String): Logger {
        return Logger(tag) { msg -> logCache.add(msg) }
    }

    fun raiseCounter(field: FieldSelector) {
        filterCounter.merge(field, 0) { old, _ -> old + 1 }
    }

    fun getTotal(): Int {
        return filterCounter.values.sum()
    }

    fun getTotal(
        packageName: String?,
        actionKind: ActionKind?,
        contentType: ContentType?,
    ): Int {
        return filterCounter.entries.filter { (key, _) ->
            (packageName == null || key.packageName == packageName) &&
                    (actionKind == null || key.actionKind == actionKind) &&
                    (contentType == null || key.contentType == contentType)
        }.fold(0) { acc, entry -> acc + entry.value }
    }
}