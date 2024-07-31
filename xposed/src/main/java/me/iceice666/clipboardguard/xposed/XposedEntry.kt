package me.iceice666.clipboardguard.xposed


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
private var rulesets = RuleSets
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

        hook(
            ClipboardManager::class.java.getDeclaredMethod("setPrimaryClip", ClipData::class.java),
            10,
            SetPrimaryClipHooker::class.java
        )

        hook(
            ClipboardManager::class.java.getDeclaredMethod("getPrimaryClip"),
            10,
            GetPrimaryClipHooker::class.java
        )
    }

    private fun applyRules(
        content: String,
        ruleset: Set<Regex>,
        fmt: (String) -> String
    ): Boolean {
        if (content.isBlank()) {
            log.info(fmt("Empty context. Ignored."))
            return false
        }

        if (ruleset.isEmpty()) {
            log.debug(fmt("Empty rule sets. Ignored."))
            return false
        }

        log.debug(fmt("Current context: $content"))

        if (ruleset.any { it.matches(content) }) {
            log.info(fmt("Rule matched. Filtered."))
            return true
        }

        log.info(fmt("No rules matched. Skipped."))
        return false
    }

    fun shouldFilter(clipData: ClipData, actionKind: ActionKind): Boolean {
        val data = clipData.getItemAt(0)

        return listOf(
            data.uri?.toString() to ContentType.Uri,
            data.intent?.toString() to ContentType.Intent,
            data.text?.toString() to ContentType.Text
        ).any { (content, contentType) ->
            content?.let {
                val fmt = { msg: String -> "($actionKind+$contentType) $msg" }
                applyRules(
                    it,
                    rulesets.request(RequestField(packageName, actionKind, contentType)),
                    fmt
                )
            } ?: false
        }


    }


    @XposedHooker
    class SetPrimaryClipHooker : Hooker {
        companion object {
            @JvmStatic
            @BeforeInvocation
            fun beforeHookedMethod(callback: BeforeHookCallback) {
                val data = callback.args[0] as ClipData
                if (module.shouldFilter(data, ActionKind.Write)) {
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
                val data = callback.result as? ClipData ?: return
                if (module.shouldFilter(data, ActionKind.Read)) {
                    callback.result = ClipData.newPlainText("", "")
                }
            }
        }
    }
}