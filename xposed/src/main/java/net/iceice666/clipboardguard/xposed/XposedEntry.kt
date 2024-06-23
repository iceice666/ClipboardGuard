package net.iceice666.clipboardguard.xposed


import android.content.ClipData
import android.content.ClipboardManager
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.Hooker
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker

private lateinit var packageName: String
private lateinit var module: XposedEntry
private val getterRulesets by lazy { HashSet<Regex>() }
private val setterRulesets by lazy { HashSet<Regex>() }
private lateinit var log: Logger

@Suppress("Unused")
class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    // Core function
    fun shouldFilter(data: ClipData, rs: RulesetTypes): Boolean {
        val fmt: (String) -> String =
            { msg ->
                when (rs) {
                    RulesetTypes.Setter -> "(Setter) "
                    RulesetTypes.Getter -> "(Getter) "
                } + msg
            }

        val rulesets: HashSet<Regex> =
            when (rs) {
                RulesetTypes.Getter -> getterRulesets
                RulesetTypes.Setter -> setterRulesets
            }

        if (rulesets.isEmpty()) {
            log.debug(fmt("Empty rule sets. Ignored."))
            return false
        }

        val text = data.getItemAt(0).text

        if (text == null) {
            log.debug(fmt("Not a text context. Ignored."))
            return false
        }

        if (text.isBlank()) {
            log.info(fmt("Empty context. Ignored."))
            return false
        }

        // Iterate through all rule sets
        log.debug(fmt("Current context: $text"))

        for (rule in rulesets) {
            if (rule.matches(text)) {
                log.info(fmt("Rule matched. Filtered."))
                return true
            }
        }

        log.info(fmt("$packageName: No rules matched. Skipped."))

        return false
    }


    init {
        module = this
        module.log("ModuleMain at " + param.processName)

    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.google.android.webview" || !param.isFirstPackage) return

        // Initialize variables
        packageName = param.packageName
        log = Logger(packageName) { msg: String -> module.log(msg) }

        // Set up rules
        ConfigLoader.getRuleSets(packageName).apply {
            getterRulesets.addAll(first)
            setterRulesets.addAll(second)
        }


        hook(
            ClipboardManager::class.java.getDeclaredMethod(
                "setPrimaryClip",
                ClipData::class.java
            ),
            SetPrimaryClipHooker::class.java
        )

        hook(
            ClipboardManager::class.java.getDeclaredMethod(
                "getPrimaryClip",
            ),
            GetPrimaryClipHooker::class.java
        )

    }

    enum class RulesetTypes {
        Getter,
        Setter
    }


    @XposedHooker
    class SetPrimaryClipHooker : Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeHookedMethod(callback: BeforeHookCallback) {
                if (module.shouldFilter(
                        callback.args[0] as ClipData,
                        RulesetTypes.Setter
                    )
                ) {
                    callback.returnAndSkip(null)
                }
            }
        }
    }

    @XposedHooker
    class GetPrimaryClipHooker : Hooker {
        companion object {
            @JvmStatic
            @AfterInvocation
            fun afterHookedMethod(callback: AfterHookCallback) {
                if (module.shouldFilter(
                        callback.result as ClipData,
                        RulesetTypes.Getter
                    )
                ) {
                    callback.result = ClipData.newPlainText("", "")
                }
            }
        }
    }
}
