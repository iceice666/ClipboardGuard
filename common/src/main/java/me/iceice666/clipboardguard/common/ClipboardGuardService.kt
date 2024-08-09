package me.iceice666.clipboardguard.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import me.iceice666.clipboardguard.common.Manager.logCache
import me.iceice666.clipboardguard.common.Manager.ruleSets
import me.iceice666.clipboardguard.common.datakind.MessagePacket
import me.iceice666.clipboardguard.common.datakind.RuleSets


class ClipboardGuardService : Service() {
    private val mBinder = object : IManagerService.Stub() {
        override fun getLogs(): MutableList<MessagePacket> {
            return logCache
        }

        override fun clearLogs() {
            return logCache.clear()
        }

        override fun getLogCount(): Int {
            return logCache.count()
        }

        override fun syncRuleSets(ruleSets: RuleSets?) {
            ruleSets?.let {
                Manager.ruleSets = it
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }
}