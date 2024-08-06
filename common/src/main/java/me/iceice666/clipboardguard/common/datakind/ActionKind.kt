package me.iceice666.clipboardguard.common.datakind

enum class ActionKind {
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