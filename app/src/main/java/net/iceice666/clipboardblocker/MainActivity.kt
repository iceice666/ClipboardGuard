package net.iceice666.clipboardblocker


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import io.github.xposed.xposedservice.IXposedService
import io.github.xposed.xposedservice.models.Application
import net.iceice666.clipboardblocker.databinding.ActivityMainBinding

class MainActivity : Activity() {
    private var ser: IXposedService? = null

    companion object {
        private const val TAG = "APIModuleTest"
    }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ibinder = getSystemService("LSPosed")
        if (ibinder != null) ser = IXposedService.Stub.asInterface(ibinder as IBinder)

        val listapp: ArrayList<Application> = ArrayList()

        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            Log.i(TAG, "switch1 $isChecked")
            if (!isChecked) {
                listapp.clear()
                return@setOnCheckedChangeListener
            }
            val app = Application()
            app.packageName = "com.discord"
            app.userId = 0
            listapp.add(app)
            for (item in listapp) {
                Log.i(TAG, item.packageName)
            }
        }

        binding.button1.setOnClickListener {
            if (ser == null) {

                Toast.makeText(
                    this,
                    "ensure your module is enabled and you have installed mywalkb/LSPosed_mod",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            try {
                Log.i(TAG, "getXposedVersionName " + ser!!.xposedVersionName)
                Log.i(TAG, "getXposedApiVersion " + ser!!.xposedApiVersion)
                Log.i(TAG, "getXposedVersionCode " + ser!!.xposedVersionCode)
                Log.i(TAG, "getApi " + ser!!.api)
                val lst: MutableList<Application> = ser!!.moduleScope
                for (app in lst) {
                    Log.i(TAG, ("getModulScope " + app.packageName) + " " + app.userId)
                }
            } catch (e: RemoteException) {
                Log.e(TAG, "getSystemService RemoteException", e)
                return@setOnClickListener
            }

        }

        binding.button2.setOnClickListener {
            try {
                ser!!.setModuleScope(listapp)
            } catch (e: RemoteException) {
                Log.e(TAG, "setModuleScope RemoteException", e)
            }
        }


    }
}