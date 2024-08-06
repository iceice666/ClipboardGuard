package me.iceice666.clipboardguard

import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

@Suppress("unused")
object LsposedService {
    var service: XposedService? = null

    init {
        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(ser: XposedService) {
                service = ser
            }

            override fun onServiceDied(ser: XposedService) {
                service = null
            }

        })
    }


}
