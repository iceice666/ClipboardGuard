package net.iceice666.clipboardblocker.xposed

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import net.iceice666.clipboardblocker.common.PACKAGE_ID


class XposedEntry : IXposedHookLoadPackage {

    private lateinit var context: Context

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName == PACKAGE_ID) return

        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            Context::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    context = param.args[0] as Context
                }
            }
        )

        XposedHelpers.findAndHookMethod(
            ClipboardManager::class.java,
            "setPrimaryClip",
            ClipData::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val clipData = param.args[0] as ClipData
                    val text = clipData.getItemAt(0).text
                    if (text == "") return

                    val packageName = lpparam.packageName
                    XposedBridge.log("$packageName: $text")
                }

            }

        )

        XposedHelpers.findAndHookMethod(
            ClipboardManager::class.java,

            // "android.content.ClipboardManager",
            // lpparam.classLoader,

            "getPrimaryClip",

            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val clipData = param.result as ClipData

                    val item = clipData.getItemAt(0)

                    if (item.intent != null) {
                        XposedBridge.log("Get a intent, ignore")
                    } else if (item.uri != null) {
                        // TODO: implement filter
                    } else if (item.text != null) {
                        // TODO: implement filter
                    } else XposedBridge.log("???")


                }

            }
        )


    }

}

