package me.iceice666.clipboardguard.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.Serializable


@Serializable
object HomepageComponent {
    @Composable
    fun Show(modifier: Modifier = Modifier) {
        WelcomeScreen(modifier)
    }

}


@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Text(text = "It's just a text")
}