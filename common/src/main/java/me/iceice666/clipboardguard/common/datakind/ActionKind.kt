package me.iceice666.clipboardguard.common.datakind

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ActionKind : Parcelable {
    Read,
    Write;

    companion object {
        fun fromString(input: String): ActionKind =
            when (input) {
                "Read" -> Read
                "Write" -> Write
                else -> throw IllegalArgumentException("Unknown action kind: $input")
            }
    }
}