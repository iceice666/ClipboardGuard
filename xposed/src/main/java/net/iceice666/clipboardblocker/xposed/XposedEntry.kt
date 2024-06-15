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

private lateinit var module: XposedEntry

@Suppress("Unused")
class XposedEntry(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {


    init {
        log("ModuleMain at " + param.processName)
        module = this
    }

//    @XposedHooker
//    class MyHooker(private val magic: Int) : XposedInterface.Hooker {
//        companion object {
//            @JvmStatic
//            @BeforeInvocation
//            fun beforeInvocation(callback: BeforeHookCallback): MyHooker {
//                val key = Random.nextInt()
//                val appContext = callback.args[0]
//                module.log("beforeInvocation: key = $key")
//                module.log("app context: $appContext")
//                return MyHooker(key)
//            }
//
//            @JvmStatic
//            @AfterInvocation
//            fun afterInvocation(callback: AfterHookCallback, context: MyHooker) {
//                module.log("afterInvocation: key = ${context.magic}")
//            }
//        }
//    }

    @XposedHooker
    class MSetPrimaryClipHooker : XposedInterface.Hooker {
        companion object {

            @JvmStatic
            @BeforeInvocation
            fun beforeHookedMethod(callback: BeforeHookCallback) {
                val clipData = callback.args[0] as ClipData
                val text = clipData.getItemAt(0).text
                if (text == "") return


                module.log("clip data: $text")
            }
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (param.packageName == "com.google.android.webview") return

        val text = "onPackageLoaded: ${param.packageName}\n" +
                "param classloader is ${param.classLoader}\n" +
                "module apk path: ${this.applicationInfo.sourceDir}\n"


        module.log(text)

        if (!param.isFirstPackage) return

        val MSetPrimaryClip = ClipboardManager::class.java.getDeclaredMethod(
            "setPrimaryClip",
            ClipData::class.java
        )
        hook(MSetPrimaryClip, MSetPrimaryClipHooker::class.java)

    }
}
