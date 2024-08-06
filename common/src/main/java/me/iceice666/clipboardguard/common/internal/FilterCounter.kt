package me.iceice666.clipboardguard.common.internal

import me.iceice666.clipboardguard.common.datakind.ActionKind
import me.iceice666.clipboardguard.common.datakind.ContentType
import me.iceice666.clipboardguard.common.datakind.FieldSelector
import me.iceice666.clipboardguard.common.Utils.ensureFolder
import java.io.File

class FilterCounter(private val rootFolder: File) {

    private var counter: MutableMap<FieldSelector, Int> = mutableMapOf()
    private val fileLock = Any()


    init {
        ensureFolder(rootFolder)
        syncCounterFromFile()
    }

    fun getTotal(): Int =
        counter.values.sum()


    fun getTotal(
        packageName: String? = null,
        actionKind: ActionKind? = null,
        contentType: ContentType? = null,
    ): Int =
        counter.entries
            .filter { (field, _) ->
                (field.packageName == packageName || packageName == null)
                        && (field.actionKind == actionKind || actionKind == null)
                        && (field.contentType == contentType || contentType == null)
            }
            .fold(0) { acc, entry -> acc + entry.value }



    fun increaseCounter(fields: FieldSelector) {
        counter.merge(fields, 1, Int::plus)
    }

    private fun syncCounterFromFile() = synchronized(fileLock) {
        rootFolder.listFiles()?.forEach { file ->
            file.readText().toIntOrNull()?.let { value ->
                counter[FieldSelector.fromString(file.name)] = value
            }
        }
    }

    fun syncCounterToFile() = synchronized(fileLock) {
        counter.forEach { (tag, count) ->
            File(rootFolder, tag.toString()).writeText(count.toString())
        }
    }
}
