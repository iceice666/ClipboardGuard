package me.iceice666.clipboardguard.xposed

import me.iceice666.clipboardguard.common.datakind.ActionKind
import me.iceice666.clipboardguard.common.datakind.ContentType
import me.iceice666.clipboardguard.common.datakind.FieldSelector
import me.iceice666.clipboardguard.common.datakind.RuleSets


object ConfigLoader {


    var ruleset: RuleSets = RuleSets()

    init {

        listOf(ContentType.Text, ContentType.Uri, ContentType.Intent).forEach {
            ruleset.update(
                FieldSelector("com.baidu.tieba", ActionKind.Read, it)
            ) { HashSet<Regex>().apply { add(Regex(".*")) } }
        }


    }

}
