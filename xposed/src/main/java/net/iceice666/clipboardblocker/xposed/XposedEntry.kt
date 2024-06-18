package net.iceice666.clipboardblocker.xposed


import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedInterface.BeforeHookCallback
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
private lateinit var packageName: String
private lateinit var module: XposedEntry

@Suppress("Unused")
class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {



    init {
        log("ModuleMain at " + param.processName)
        module = this
    }


    @XposedHooker
    class SetPrimaryClipHooker : XposedInterface.Hooker {
        companion object {

            @JvmStatic
            @BeforeInvocation
            fun beforeHookedMethod(callback: BeforeHookCallback) {
                val clipData = callback.args[0] as ClipData
                val text = clipData.getItemAt(0).text
                if (text == "") return




                module.log("$packageName: $text")
            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.google.android.webview") return

        if (!param.isFirstPackage) return

        packageName = param.packageName


        hook(
            ClipboardManager::class.java.getDeclaredMethod(
                "setPrimaryClip",
                ClipData::class.java
            ),
            SetPrimaryClipHooker::class.java
        )

    }
}
