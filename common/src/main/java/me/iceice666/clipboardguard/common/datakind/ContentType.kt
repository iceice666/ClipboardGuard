package me.iceice666.clipboardguard.common.datakind

enum class ContentType {
    Intent,
    Uri,
    Text;

    companion object {
        fun fromString(input: String): ContentType =
            when (input) {
                "Intent" -> Intent
                "Uri" -> Uri
                "Text" -> Text
                else -> throw IllegalArgumentException("Unknown content type: $input")
            }
    }
}