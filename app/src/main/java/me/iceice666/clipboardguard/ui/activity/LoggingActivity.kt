package me.iceice666.clipboardguard.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.libxposed.service.XposedService
import me.iceice666.clipboardguard.LsposedServiceManager
import me.iceice666.clipboardguard.common.RemotePreferences
import me.iceice666.clipboardguard.ui.activity.ui.theme.ClipboardGuardTheme

class LoggingActivity : ComponentActivity() {

    private lateinit var rawLogs: Map<String, *>

    private val service: XposedService? = LsposedServiceManager.service

    private fun refreshLog() {
        rawLogs =
            service?.getRemotePreferences(RemotePreferences.LOGGING)?.all ?: emptyMap<String, Any>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshLog()
        enableEdgeToEdge()

        setContent {
            ClipboardGuardTheme {
                //         MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }


}

@Composable
fun LogContainer(
    modifier: Modifier = Modifier,
    logs: Map<String, *>
) {

    Column(modifier = modifier.padding(vertical = 4.dp)) {
        for ((key, value) in logs) {
            LogItem(key, value.toString())
        }
    }

}


@Composable
fun LogItem(time: String, log: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        val (level, packageName, message, cause) = log.split("$$", limit = 4)

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(text = level)
            Text(text = time.replace("_", " "))
            Text(text = packageName)
            Text(text = message)
            if (cause.isNotBlank()) Text(text = cause)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    ClipboardGuardTheme {
        LogContainer(
            logs = mapOf(
                "1970-01-01_00:00:00.000" to
                        "Debug$\$com.discord$$(Read+Text) Empty rule sets. Ignored.$$",
            )
        )
    }
}