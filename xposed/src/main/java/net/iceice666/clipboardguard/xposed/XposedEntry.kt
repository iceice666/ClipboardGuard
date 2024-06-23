package net.iceice666.clipboardguard.xposed


import android.annotation.SuppressLint
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
private lateinit var getRule: HashSet<Regex>
private lateinit var setRule: HashSet<Regex>
private lateinit var log: Logger

@Suppress("Unused")
class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {



    init {
        module = this
        module.log("ModuleMain at " + param.processName)

    }


    @XposedHooker
    class SetPrimaryClipHooker : Hooker {
        companion object {

            @JvmStatic
            @BeforeInvocation
            fun beforeHookedMethod(callback: BeforeHookCallback) {

                if (getRule.isEmpty()) {
                    module.log("$packageName: Empty rule sets. Ignored.")
                    return
                }

                val clipData = callback.args[0] as ClipData
                val text = clipData.getItemAt(0).text

                if (text == null) {
                    module.log("$packageName: Not a text context. Ignored.")
                }

                if (text == "") {
                    module.log("$packageName: Empty context. Ignored.")
                }

                // Iterate through all rule sets
                module.log("$packageName: Current context: $text")
                for (rule in getRule) {
                    if (rule.matches(text)) {
                        module.log("$packageName: Rule matched. Filtered.")
                        callback.returnAndSkip(null)
                    }
                }

                module.log("$packageName: No rules matched. Skipped.")


            }
        }
    }

    @XposedHooker
    class GetPrimaryClipHooker : Hooker {
        companion object {

            @JvmStatic
            @AfterInvocation
            fun afterHookedMethod(callback: AfterHookCallback) {
                if (getRule.isEmpty()) {
                    module.log("$packageName: Empty rule sets. Ignored.")
                    return
                }


                val result = callback.result as ClipData
                val text = result.getItemAt(0).text

                if (text == null) {
                    module.log("$packageName: Not a text context. Ignored.")
                }

                if (text == "") {
                    module.log("$packageName: Empty context. Ignored.")
                }

                // Iterate through all rule sets
                module.log("$packageName: Current context: $text")
                for (rule in getRule) {
                    if (rule.matches(text)) {
                        module.log("$packageName: Rule matched. Filtered.")
                        callback.result = ClipData.newPlainText("", "")
                    }
                }

                module.log("$packageName: No rules matched. Skipped.")


            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.google.android.webview") return

        if (!param.isFirstPackage) return

        packageName = param.packageName
        log = Logger(packageName)

        getRule = HashSet()
        setRule = HashSet()


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
}
