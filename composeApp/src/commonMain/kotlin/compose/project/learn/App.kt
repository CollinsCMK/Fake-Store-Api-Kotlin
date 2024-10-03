package compose.project.learn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import compose.project.learn.screen.home.HomeScreen
import compose.project.learn.tabs.home.HomeTab
import compose.project.learn.tabs.profile.ProfileTab
import compose.project.learn.tabs.settings.SettingsTab
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    Box(
        modifier = Modifier
            .background(Color.Blue)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        MaterialTheme {
            TabNavigator(HomeTab) { navigator ->
                Scaffold(
                    bottomBar = {
                        BottomNavigation(
                            backgroundColor = Color.Blue

                        ) {
                            TabNavigationItem(HomeTab)
                            TabNavigationItem(ProfileTab)
                            TabNavigationItem(SettingsTab)
                        }
                    }
                ) {
                    CurrentTab()
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title, color = Color.White) },
        icon = { tab.options.icon?.let { Icon(it, contentDescription = null) } },
        selectedContentColor = Color.White,
        unselectedContentColor = Color.Gray
    )
}
