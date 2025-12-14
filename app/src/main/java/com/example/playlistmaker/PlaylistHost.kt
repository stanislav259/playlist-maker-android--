package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.MainScreen
import com.example.playlistmaker.Screen
import com.example.playlistmaker.SearchScreen
import com.example.playlistmaker.SettingsScreen

@Composable
fun PlaylistHost() {
    val navController = rememberNavController()
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val navigateToSearch = { navController.navigate(Screen.Search.route) }
    val navigateToSettings = { navController.navigate(Screen.Settings.route) }
    val navigateToPlaylists = { /* пока ничего */ }
    val navigateToFavorites = { /* пока ничего */ }


    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                onSearchClick = navigateToSearch,
                onPlaylistsClick = navigateToPlaylists,
                onFavoritesClick = navigateToFavorites,
                onSettingsClick = navigateToSettings
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = navigateBack
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = navigateBack
            )
        }
    }
}