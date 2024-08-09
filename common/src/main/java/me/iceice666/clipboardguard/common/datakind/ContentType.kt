package me.iceice666.clipboardguard.common.datakind
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ContentType : Parcelable{
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