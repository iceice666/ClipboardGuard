package me.iceice666.clipboardguard.common.datakind

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegexSet( val data: MutableSet<Regex> = emptySet<Regex>().toMutableSet()) : Parcelable

@Parcelize
data class RuleSets(
    private val inner: MutableMap<FieldSelector, RegexSet> = mutableMapOf()
) : Parcelable {
    fun request(field: FieldSelector): RegexSet {
        return inner.getOrDefault(field, RegexSet())
    }

    fun update(field: FieldSelector, modifier: (RegexSet) -> Unit) {
        modifier(request(field))
    }

    fun replace(new: RuleSets) {
        inner.clear()
        inner.putAll(new.inner)
    }
}