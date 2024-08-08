package me.iceice666.clipboardguard.xposed.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import me.iceice666.clipboardguard.common.IManagerService

class ClipboardGuardServiceClient() {
    private var mService: IManagerService? = null
    private var isBound = false

    fun getLogger(tag: String): Logger = Logger(tag) { msg ->
        operateWithService { service ->
            service.writeLog(msg)
        }
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = IManagerService.Stub.asInterface(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            isBound = false
        }
    }

    fun bindService(context: Context) {
        val intent = Intent()
        intent.setComponent(
            ComponentName(
                "me.iceice666.clipboardguard.app",
                "me.iceice666.clipboardguard.app.ManagerService"
            )
        )
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)

    }

    fun unbindService(context: Context) {
        if (isBound) {
            context.unbindService(connection)
            isBound = false
        }
    }

    @Throws(RemoteException::class)
    fun <ResultType> operateWithService(operation: (IManagerService) -> ResultType): ResultType? {
        return if (isBound) operation(mService!!) else null
    }


}
