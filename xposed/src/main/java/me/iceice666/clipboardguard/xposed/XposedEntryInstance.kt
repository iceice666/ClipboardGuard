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


private lateinit var module: XposedEntryInstance

class XposedEntryInstance(base: XposedInterface, param: ModuleLoadedParam) :
    XposedModule(base, param) {

    lateinit var packageName: String
    private lateinit var log: Logger

    init {
        module = this
        module.log("ModuleMain at process" + param.processName)
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.google.android.webview" || !param.isFirstPackage) return

        packageName = param.packageName
        log = Logger(this)

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

    enum class ApplyResult {
        EmptyContent,
        EmptyRuleset,
        Matched,
        NotMatched;

        override fun toString(): String {
            return when (this) {
                EmptyContent -> "Empty content. Ignored."
                EmptyRuleset -> "Empty rule sets. Ignored."
                Matched -> "Rule matched. Filtered."
                NotMatched -> "No rules matched. Skipped."
            }
        }

    }

    private fun applyRules(content: String, ruleset: Set<Regex>): ApplyResult {
        return if (content.isBlank()) {
            ApplyResult.EmptyContent
        } else if (ruleset.isEmpty()) {
            ApplyResult.EmptyRuleset
        } else if (ruleset.any { it.matches(content) }) {
            ApplyResult.Matched
        } else {
            ApplyResult.NotMatched
        }
    }

    fun shouldFilter(clipData: ClipData, actionKind: ActionKind): Boolean {
        val clipItem = clipData.getItemAt(0)

        val (contentType, content) = when {
            clipItem.uri != null -> ContentType.Uri to clipItem.uri.toString()
            clipItem.intent != null -> ContentType.Intent to clipItem.intent.toString()
            clipItem.text != null -> ContentType.Text to clipItem.text.toString()
            else -> return false
        }

        val ruleset = RuleSets.request(RequestField(packageName, actionKind, contentType))
        val result = applyRules(content, ruleset)

        log.info("($actionKind+$contentType):$result")

        if (result == ApplyResult.Matched) {
            val pref = getRemotePreferences(Constants.RemotePreferences.FILTERER_COUNTER)
            val prefKey = "${packageName}.$actionKind+$contentType"
            val origin = pref.getLong(prefKey, 0)
            pref.edit().putLong(prefKey, origin + 1).apply()
        }

        return result == ApplyResult.Matched
    }


    @Suppress("Unused")
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

    @Suppress("Unused")
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