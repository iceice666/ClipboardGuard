package me.iceice666.clipboardguard.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RuleSets(
    private val inner: HashMap<FieldSelector, HashSet<Regex>> = HashMap()
) : Parcelable {
    fun request(field: FieldSelector): HashSet<Regex> {
        return inner.getOrDefault(field, HashSet())
    }

    fun update(field: FieldSelector, modifier: (HashSet<Regex>) -> HashSet<Regex>) {
        modifier(request(field))
    }
}