package net.iceice666.clipboardguard.xposed


object ConfigLoader {

    private var getRuleSets: HashMap<String, Ruleset>
    private var setRuleSets: HashMap<String, Ruleset>

    init {

        getRuleSets = HashMap<String, Ruleset>().apply {
            put(
                "com.baidu.tieba",
                Ruleset(
                    text = HashSet<Regex>().apply { add(Regex(".*")) }
                )
            )
        }

        setRuleSets = HashMap<String, Ruleset>().apply {

        }
    }


    fun getRuleSets(packageName: String): Pair<Ruleset, Ruleset> {
        val a = getRuleSets[packageName] ?: Ruleset()
        val b = setRuleSets[packageName] ?: Ruleset()
        return Pair(a, b)
    }


}

data class Ruleset(
    val intent: HashSet<Regex> = HashSet(),
    val uri: HashSet<Regex> = HashSet(),
    val text: HashSet<Regex> = HashSet()
) {

}