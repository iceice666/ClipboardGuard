package me.iceice666.clipboardguard.xposed


import android.content.ClipData
import android.content.ClipboardManager
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.AfterHookCallback
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedInterface.Hooker
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import me.iceice666.clipboardguard.common.BuildConfig.PACKAGE_ID
import me.iceice666.clipboardguard.common.Logger
import me.iceice666.clipboardguard.common.Manager
import me.iceice666.clipboardguard.common.Manager.logCache
import me.iceice666.clipboardguard.common.datakind.ActionKind
import me.iceice666.clipboardguard.common.datakind.ContentType
import me.iceice666.clipboardguard.common.datakind.FieldSelector
import me.iceice666.clipboardguard.common.datakind.RegexSet


private lateinit var module: XposedEntry

class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) :
    XposedModule(base, param) {

    private lateinit var packageName: String
    private lateinit var logger: Logger

    private val whitelist = listOf(PACKAGE_ID)

    init {
        module = this
        module.log("ModuleMain at process: " + param.processName)
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        val packageName = param.packageName

        if (
            whitelist.any { it == packageName }
            || packageName.startsWith("com.android")
            || !param.isFirstPackage
        ) return


        this.packageName = packageName

        module.log("Package loaded: $packageName")
        logger = Manager.getLogger(packageName)

        hookMethods()
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

    private fun applyRules(content: String, ruleset: RegexSet): ApplyResult =
        when (true) {
            content.isBlank() -> ApplyResult.EmptyContent
            ruleset.data.isEmpty() -> ApplyResult.EmptyRuleset
            ruleset.data.any { it.matches(content) } -> ApplyResult.Matched
            else -> ApplyResult.NotMatched
        }


    fun shouldFilter(clipData: ClipData, actionKind: ActionKind): Boolean {
        val clipItem = clipData.getItemAt(0)

        val (contentType, content) = when {
            clipItem.uri != null -> ContentType.Uri to clipItem.uri.toString()
            clipItem.intent != null -> ContentType.Intent to clipItem.intent.toString()
            clipItem.text != null -> ContentType.Text to clipItem.text.toString()
            else -> return false
        }

        val field = FieldSelector(packageName, actionKind, contentType)

        val ruleset = Manager.ruleSets.request(field)
        val result = applyRules(content, ruleset)


        module.log("[$packageName]($actionKind+$contentType):$result")
        logger.info("[$packageName]($actionKind+$contentType):$result")

        module.log(logCache.count().toString())

        if (result == ApplyResult.Matched) {
            Manager.raiseCounter(field)
        }


        return result == ApplyResult.Matched
    }

    private fun hookMethods() {
        hook(
            ClipboardManager::class.java.getDeclaredMethod("setPrimaryClip", ClipData::class.java),
            SetPrimaryClipHooker::class.java
        )

        hook(
            ClipboardManager::class.java.getDeclaredMethod("getPrimaryClip"),
            GetPrimaryClipHooker::class.java
        )

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

//    @Suppress("Unused")
//    @XposedHooker
//    class ApplicationHooker : Hooker {
//        companion object {
//            @JvmStatic
//            @AfterInvocation
//            fun afterHookedMethod(callback: AfterHookCallback) {
//                context = callback.args[0] as Context
//            }
//        }
//    }


}