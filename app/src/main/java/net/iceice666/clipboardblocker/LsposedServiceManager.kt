package net.iceice666.clipboardblocker

import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

@Suppress("unused")
object LsposedServiceManager {

    private var service: XposedService? = null

    fun getInstance(): XposedService? {
        if (service == null) {
            XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(ser: XposedService) {
                    service = ser
                }

                override fun onServiceDied(ser: XposedService) {
                    service = null
                }

            })
        }

        return service
    }
}
