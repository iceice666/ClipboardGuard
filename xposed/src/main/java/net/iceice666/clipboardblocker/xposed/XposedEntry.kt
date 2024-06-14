package net.iceice666.clipboardblocker.xposed

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedEntry : IXposedHookLoadPackage{
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        TODO("Not yet implemented")
    }
}