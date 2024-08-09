package me.iceice666.clipboardguard.common.datakind

import kotlinx.serialization.Serializable

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Serializable
data class FieldSelector(
    val packageName: String,
    val actionKind: ActionKind,
    val contentType: ContentType,
) : Parcelable{
    override fun toString(): String =
        "${packageName}_$actionKind-$contentType"

    companion object {
        fun fromString(input: String): FieldSelector {
            val (packageName, actionKind, contentType) =
                input.split(Regex("([a-zA-Z0-9_.]+)_([a-zA-Z]+)-([a-zA-Z]+)"))

            return FieldSelector(
                packageName,
                ActionKind.fromString(actionKind),
                ContentType.fromString(contentType)
            )
        }
    }
}

