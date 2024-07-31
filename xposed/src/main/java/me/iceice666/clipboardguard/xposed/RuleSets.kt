package me.iceice666.clipboardguard.xposed

data class RequestField(
    val packageName: String,
    val actionKind: ActionKind,
    val contentType: ContentType,
)

enum class ActionKind {
    Read,
    Write
}

enum class ContentType {
    Intent,
    Uri,
    Text
}

object RuleSets {
    private val inner: HashMap<RequestField, HashSet<Regex>> = HashMap()

    fun request(field: RequestField): HashSet<Regex> {
        return inner.getOrDefault(field, HashSet())
    }

    fun update(field: RequestField, modifier: (HashSet<Regex>) -> HashSet<Regex>) {
        modifier(request(field))
    }


}