package me.iceice666.clipboardguard.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegexSet( val data: Set<Regex> = emptySet()) : Parcelable

@Parcelize
data class RuleSets(
    private val inner: Map<FieldSelector, RegexSet> = HashMap()
) : Parcelable {
    fun request(field: FieldSelector): RegexSet {
        return inner.getOrDefault(field, RegexSet())
    }

    fun update(field: FieldSelector, modifier: (RegexSet) -> RegexSet) {
        modifier(request(field))
    }
}