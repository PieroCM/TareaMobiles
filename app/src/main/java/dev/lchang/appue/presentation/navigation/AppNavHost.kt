package dev.lchang.appue.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.lchang.appue.presentation.home.HomeScreen
import dev.lchang.appue.presentation.home.LibraryScreen
import dev.lchang.appue.presentation.home.PlaylistScreen
import dev.lchang.appue.presentation.home.SearchScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val items = listOf(
        BottomItem(AppDestinations.HOME, "Home") { Icon(Icons.Default.Home, contentDescription = "Home") },
        BottomItem(AppDestinations.SEARCH, "Search") { Icon(Icons.Default.Search, contentDescription = "Search") },
        BottomItem(AppDestinations.LIBRARY, "Library") { Icon(Icons.Default.VideoLibrary, contentDescription = "Library") }
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val currentRoute = currentDestination?.route

            // Solo mostramos la barra inferior cuando estamos en una pantalla principal
            val showBottomBar = currentRoute == AppDestinations.HOME ||
                               currentRoute == AppDestinations.SEARCH ||
                               currentRoute == AppDestinations.LIBRARY

            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                // Navegación mejorada para asegurar que Home siempre regresa al inicio
                                navController.navigate(item.route) {
                                    // Siempre volver a la raíz al navegar
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = item.icon,
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { inner ->
        NavHost(
            navController = navController,
            startDestination = AppDestinations.HOME,
            modifier = Modifier.padding(inner)
        ) {
            composable(AppDestinations.HOME) {
                HomeScreen(onPlaylistClick = { id ->
                    navController.navigate(AppDestinations.playlistRoute(id))
                })
            }
            composable(AppDestinations.SEARCH) { SearchScreen() }
            composable(AppDestinations.LIBRARY) { LibraryScreen() }
            composable("${AppDestinations.PLAYLIST}/{${AppDestinations.PLAYLIST_ID_ARG}}") { backStack ->
                val id = backStack.arguments?.getString(AppDestinations.PLAYLIST_ID_ARG) ?: return@composable
                PlaylistScreen(
                    playlistId = id,
                    onBack = { navController.popBackStack() },
                    onHomeClick = {
                        // Añadido: Navegación directa a Home desde cualquier pantalla
                        navController.navigate(AppDestinations.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = false
                            }
                        }
                    }
                )
            }
        }
    }
}

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)
