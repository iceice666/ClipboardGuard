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
private lateinit var getterRulesets: Ruleset
private lateinit var setterRulesets: Ruleset
private lateinit var log: Logger

@Suppress("Unused")
class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    init {
        module = this
        module.log("ModuleMain at " + param.processName)
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.google.android.webview" || !param.isFirstPackage) return

        packageName = param.packageName
        log = Logger(packageName) { msg: String -> module.log(msg) }

        ConfigLoader.getRuleSets(packageName).apply {
            getterRulesets = first
            setterRulesets = second
        }

        hook(
            ClipboardManager::class.java.getDeclaredMethod("setPrimaryClip", ClipData::class.java),
            SetPrimaryClipHooker::class.java
        )

        hook(
            ClipboardManager::class.java.getDeclaredMethod("getPrimaryClip"),
            GetPrimaryClipHooker::class.java
        )
    }

    fun shouldFilter(clipData: ClipData, rst: RulesetTypes): Boolean {
        val fmt = { msg: String -> "(${rst.name}) $msg" }
        val rulesets = when (rst) {
            RulesetTypes.Getter -> getterRulesets
            RulesetTypes.Setter -> setterRulesets
        }

        val data = clipData.getItemAt(0)
        val contexts =
            listOfNotNull(data.intent?.toString(), data.uri?.toString(), data.text?.toString())

        for (context in contexts) {
            if (context.isBlank()) {
                log.info(fmt("Empty context. Ignored."))
                return false
            }

            val ruleset = when (context) {
                data.intent?.toString() -> rulesets.intent
                data.uri?.toString() -> rulesets.uri
                data.text?.toString() -> rulesets.text
                else -> emptySet()
            }

            if (ruleset.isEmpty()) {
                log.debug(fmt("Empty rule sets. Ignored."))
                return false
            }

            log.debug(fmt("Current context: $context"))

            if (ruleset.any { it.matches(context) }) {
                log.info(fmt("Rule matched. Filtered."))
                return true
            }
        }

        log.info(fmt("No rules matched. Skipped."))
        return false
    }

    enum class RulesetTypes {
        Getter, Setter
    }

    @XposedHooker
    class SetPrimaryClipHooker : Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeHookedMethod(callback: BeforeHookCallback) {
                if (module.shouldFilter(callback.args[0] as ClipData, RulesetTypes.Setter)) {
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
                        callback.result as? ClipData ?: return,
                        RulesetTypes.Getter
                    )
                ) {
                    callback.result = ClipData.newPlainText("", "")
                }
            }
        }
    }
}