package me.iceice666.clipboardguard.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.iceice666.clipboardguard.app.component.BottomNavigationBar
import me.iceice666.clipboardguard.app.ui.theme.ClipboardGuardTheme

const val TAG = "ClipboardGuardApp"


class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClipboardGuardTheme {
                RootContainer()
            }
        }



    }


}


@Composable
fun RootContainer(modifier: Modifier = Modifier) {
    BottomNavigationBar(modifier)

}


