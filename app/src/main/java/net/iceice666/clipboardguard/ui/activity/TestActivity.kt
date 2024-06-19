package net.iceice666.clipboardguard.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedService.OnScopeEventListener
import net.iceice666.clipboardguard.databinding.ActivityTestBinding
import net.iceice666.clipboardguard.LsposedServiceManager


class TestActivity : Activity() {

    private val service: XposedService? = LsposedServiceManager.getInstance()

    private val mCallback = object : OnScopeEventListener {
        override fun onScopeRequestPrompted(packageName: String) {
            runOnUiThread {
                showToast("onScopeRequestPrompted: $packageName")
            }
        }

        override fun onScopeRequestApproved(packageName: String) {
            runOnUiThread {
                showToast("onScopeRequestApproved: $packageName")
            }
        }

        override fun onScopeRequestDenied(packageName: String) {
            runOnUiThread {
                showToast("onScopeRequestDenied: $packageName")
            }
        }

        override fun onScopeRequestTimeout(packageName: String) {
            runOnUiThread {
                showToast("onScopeRequestTimeout: $packageName")
            }
        }

        override fun onScopeRequestFailed(packageName: String, message: String) {
            runOnUiThread {
                showToast("onScopeRequestFailed: $packageName, $message")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@TestActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun refreshScope(binding: ActivityTestBinding, scope: List<String>) {
        binding.scopeList.text = scope.joinToString("\n")
    }

    @SuppressLint("SetTextI18n")
    private fun updateInfo(binding: ActivityTestBinding) {
        service?.let { service ->
            binding.binder.text = "Binder acquired"
            binding.api.text = "API: ${service.apiVersion}"
            binding.framworkInfo.text =
                "${service.frameworkName} ${service.frameworkVersion} [code ${service.frameworkVersionCode}]"

            refreshScope(binding, service.scope)

            binding.requestScope.setOnClickListener {
                val app = binding.scopeInput.text.toString().trim()
                if (app.isEmpty()) {
                    showToast("App name is empty")
                    return@setOnClickListener
                }
                service.requestScope(app, mCallback)
            }

            binding.removeScope.setOnClickListener {
                val app = binding.scopeInput.text.toString().trim()
                if (app.isEmpty()) {
                    showToast("App name is empty")
                    return@setOnClickListener
                }
                val res = service.removeScope(app)
                res?.let {
                    runOnUiThread {
                        showToast(it)
                    }
                }
            }

            binding.refreshScope.setOnClickListener {
                refreshScope(binding, service.scope)
            }
        } ?: run {
            binding.binder.text = "Binder is null!\nPlease restart the app"
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.binder.text = "Loading"

        updateInfo(binding)

        Handler(Looper.getMainLooper()).postDelayed({
            service?.let { updateInfo(binding) }
        }, 5000)
    }
}
