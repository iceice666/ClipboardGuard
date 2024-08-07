package me.iceice666.clipboardguard.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import me.iceice666.clipboardguard.common.FieldSelector
import me.iceice666.clipboardguard.common.IManagerService
import me.iceice666.clipboardguard.common.MessagePacket
import me.iceice666.clipboardguard.common.RuleSets


class ManagerService : Service() {
    private val binder: IManagerService.Stub = object : IManagerService.Stub() {
        @Throws(RemoteException::class)
        override fun writeLog(message: MessagePacket?) {
            TODO("Not yet implemented")
        }

        override fun requestRuleSets(field: FieldSelector?): RuleSets {
            TODO("Not yet implemented")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }


}