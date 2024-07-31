package me.iceice666.clipboardguard.xposed


object ConfigLoader {


    private var ruleset: RuleSets = RuleSets

    init {

        ruleset.update(
            RequestField("com.baidu.tieba", ActionKind.Read, ContentType.Text)
        )
        { HashSet<Regex>().apply { add(Regex(".*")) } }


    }

}
