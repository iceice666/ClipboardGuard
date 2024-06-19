package net.iceice666.clipboardguard.xposed


object ConfigLoader {

    private var getRuleSets: HashMap<String, HashSet<Regex>>
    private var setRuleSets: HashMap<String, HashSet<Regex>>

    init {

        getRuleSets = HashMap<String, HashSet<Regex>>().apply {
            put("com.baidu.tieba", HashSet<Regex>().apply { add(Regex(".*")) })
        }

        setRuleSets = HashMap<String, HashSet<Regex>>().apply {

        }
    }


    fun getRuleSets(packageName: String): Pair<HashSet<Regex>, HashSet<Regex>> {
        val a = getRuleSets[packageName] ?: HashSet()
        val b = setRuleSets[packageName] ?: HashSet()
        return Pair(a, b)
    }


}