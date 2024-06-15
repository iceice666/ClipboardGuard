package net.iceice666.clipboardblocker.activity


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast

import net.iceice666.clipboardblocker.databinding.ActivityMainBinding

import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedService.OnScopeEventListener
import io.github.libxposed.service.XposedServiceHelper
import net.iceice666.clipboardblocker.LsposedServiceManager

class MainActivity : Activity() {

    private var service: XposedService? = LsposedServiceManager.getInstance()

    private val mCallback = object : OnScopeEventListener {
        override fun onScopeRequestPrompted(packageName: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "onScopeRequestPrompted: $packageName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onScopeRequestApproved(packageName: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "onScopeRequestApproved: $packageName",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        override fun onScopeRequestDenied(packageName: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "onScopeRequestDenied: $packageName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onScopeRequestTimeout(packageName: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "onScopeRequestTimeout: $packageName",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onScopeRequestFailed(packageName: String, message: String) {
            runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "onScopeRequestFailed: $packageName, $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun refreshScope(binding: ActivityMainBinding, scope: MutableList<String>) {
        var text = ""
        for (app in scope) {
            text += app + "\n"
        }
        binding.scopeList.text = text
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.binder.text = "Loading"

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            service?.let { service ->
                binding.binder.text = "Binder acquired"
                binding.api.text = "API: ${service.apiVersion}"
                binding.framworkInfo.text =
                    "${service.frameworkName} ${service.frameworkVersion} [code ${service.frameworkVersionCode}]"

                refreshScope(binding, service.scope)

                binding.requestScope.setOnClickListener {
                    val app = binding.scopeInput.text.toString().trim()
                    if (app.isEmpty()) {
                        Toast.makeText(this@MainActivity, "App name is empty", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    service.requestScope(app, mCallback)
                }

                binding.removeScope.setOnClickListener {
                    val app = binding.scopeInput.text.toString().trim()
                    if (app.isEmpty()) {
                        Toast.makeText(this@MainActivity, "App name is empty", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }
                    val res = service.removeScope(app)

                    if (res != null) {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, res, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                binding.refreshScope.setOnClickListener {
                    refreshScope(binding, service.scope)
                }
            } ?: run {
                binding.binder.text = "Binder is null!\nPlease restart the app"
            }

        }, 5000)
    }
}