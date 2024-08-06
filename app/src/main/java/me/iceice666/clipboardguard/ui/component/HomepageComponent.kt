package me.iceice666.clipboardguard.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.libxposed.service.XposedService
import kotlinx.serialization.Serializable
import me.iceice666.clipboardguard.LsposedServiceManager


@Serializable
object HomepageComponent : IDestinationComponent {
    @Composable
    override fun Show(modifier: Modifier) {
        Homepage(modifier)
    }

    override val label: String = "home"
    override val icon: ImageVector = Icons.Filled.Home

}

private val service: XposedService? = LsposedServiceManager.service

@Preview
@Composable
fun Homepage(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            service?.let { service ->
                Text(text = "Binder acquired", style = MaterialTheme.typography.titleLarge)
                Text(text = "API: ${service.apiVersion}")
                Text(text = "${service.frameworkName} ${service.frameworkVersion} [code ${service.frameworkVersionCode}]")

            } ?: Text(text = "Binder is null!")


        }
    }
}
