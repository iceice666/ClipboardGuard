package me.iceice666.clipboardguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import me.iceice666.clipboardguard.ui.component.HomepageComponent

import me.iceice666.clipboardguard.ui.component.LoggingComponent
import me.iceice666.clipboardguard.ui.theme.ClipboardGuardTheme


class MainActivity : ComponentActivity() {

    private val service = LsposedServiceManager.service

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
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HomepageComponent) {
        composable<LoggingComponent> { backStackEntry ->
            backStackEntry.toRoute<LoggingComponent>().Show(modifier = modifier)
        }

        composable<HomepageComponent> {
            HomepageComponent.Show(modifier = modifier)
        }
    }


}