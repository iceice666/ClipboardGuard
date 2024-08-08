package me.iceice666.clipboardguard.xposed


import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
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
import me.iceice666.clipboardguard.common.ActionKind
import me.iceice666.clipboardguard.common.BuildConfig.PACKAGE_ID
import me.iceice666.clipboardguard.common.ContentType
import me.iceice666.clipboardguard.common.FieldSelector
import me.iceice666.clipboardguard.common.RegexSet
import me.iceice666.clipboardguard.xposed.service.ClipboardGuardServiceClient
import me.iceice666.clipboardguard.xposed.service.Logger


private lateinit var module: XposedEntry

class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) :
    XposedModule(base, param) {

    private lateinit var packageName: String
    private var mService: ClipboardGuardServiceClient = ClipboardGuardServiceClient()
    private lateinit var logger: Logger

    private val whitelist = listOf(PACKAGE_ID, "com.google.android.webview")

    init {
        module = this
        module.log("ModuleMain at process: " + param.processName)
    }

    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (whitelist.any { it == param.packageName } || !param.isFirstPackage) return

        packageName = param.packageName
        logger = mService.getLogger(packageName)

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

        val ruleset =
            mService.operateWithService<RegexSet> { service -> service.requestRuleSets(field) }
        val result = applyRules(content, ruleset ?: RegexSet())

        logger.info("($actionKind+$contentType):$result")

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

        hook(
            Application::class.java.getDeclaredMethod("attachBaseContext", Context::class.java),
            ApplicationHooker::class.java
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

    @Suppress("Unused")
    @XposedHooker
    class ApplicationHooker : Hooker {
        companion object {
            @JvmStatic
            @AfterInvocation
            fun afterHookedMethod(callback: AfterHookCallback) {
                val application = callback.thisObject as Application
                application.registerActivityLifecycleCallbacks(
                    object : Application.ActivityLifecycleCallbacks {
                        override fun onActivityCreated(
                            activity: Activity,
                            savedInstanceState: Bundle?
                        ) {
                            module.mService.bindService(application.baseContext)
                        }

                        override fun onActivityStarted(activity: Activity) {}

                        override fun onActivityResumed(activity: Activity) {}

                        override fun onActivityPaused(activity: Activity) {}

                        override fun onActivityStopped(activity: Activity) {}

                        override fun onActivitySaveInstanceState(
                            activity: Activity,
                            outState: Bundle
                        ) {
                        }

                        override fun onActivityDestroyed(activity: Activity) {
                            module.mService.unbindService(application.baseContext)
                        }
                    }
                )
            }
        }
    }


}