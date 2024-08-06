package me.iceice666.clipboardguard.ui.component

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

interface IDestinationComponent {
    @Composable
    fun Show(modifier: Modifier)

    val route: String
        get() = this::class.java.name

    val label: String
    val icon: ImageVector
}

enum class Destination(val component: IDestinationComponent) {
    Home(HomepageComponent),
    Logging(LoggingComponent)
}

@Preview
@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier) {

    var navigationSelectedItem by remember { mutableIntStateOf(0) }
    val items = Destination.entries.toTypedArray()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    @Composable
    fun NavigationHost(modifier: Modifier) =
        NavHost(navController = navController, startDestination = HomepageComponent) {
            composable<LoggingComponent> {
                LoggingComponent.Show(modifier = modifier)
            }

            composable<HomepageComponent> {
                HomepageComponent.Show(modifier = modifier)
            }
        }

    @Composable
    fun NavigationBar() = NavigationBar {
        items.forEachIndexed { index, item ->
            val component = item.component
            NavigationBarItem(
                icon = { Icon(component.icon, contentDescription = component.label) },
                label = { Text(component.label) },
                selected = currentDestination?.hierarchy?.any { it.route == component.route } == true,
                onClick = {
                    navigationSelectedItem = index
                    navController.navigate(component) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }


    Scaffold(modifier = modifier, bottomBar = { NavigationBar() }) { paddingValues ->
        NavigationHost(modifier = modifier.padding(paddingValues))
    }
}