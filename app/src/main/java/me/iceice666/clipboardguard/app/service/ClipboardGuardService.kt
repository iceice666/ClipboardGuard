package me.iceice666.clipboardguard.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import me.iceice666.clipboardguard.common.FieldSelector
import me.iceice666.clipboardguard.common.IManagerService
import me.iceice666.clipboardguard.common.MessagePacket
import me.iceice666.clipboardguard.common.RegexSet


val logs = mutableListOf<MessagePacket>()

class ClipboardGuardService : Service() {
    companion object {
        const val TAG = "ClipboardGuardService"
    }

    private val binder: IManagerService.Stub = object : IManagerService.Stub() {
        @Throws(RemoteException::class)
        override fun writeLog(message: MessagePacket) {
            logs.add(message)
        }

        override fun requestRuleSets(field: FieldSelector): RegexSet {
            return RegexSet()
        }
    }

    override fun onCreate() {
        super.onCreate()

//        TODO("Load config")
//        TODO("Clean up logs")
//        TODO("Load counter")

        Log.d(TAG, "Service Created")
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "New client: $intent")
        return binder
    }


}