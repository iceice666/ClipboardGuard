package me.iceice666.clipboardguard.common.datakind


data class RuleSets(
    private val inner: HashMap<FieldSelector, HashSet<Regex>> = HashMap()
) {
    fun request(field: FieldSelector): HashSet<Regex> {
        return inner.getOrDefault(field, HashSet())
    }

    fun update(field: FieldSelector, modifier: (HashSet<Regex>) -> HashSet<Regex>) {
        modifier(request(field))
    }
}