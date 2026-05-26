package com.example.playlistmaker.ui.activity

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.Screen
import com.example.playlistmaker.SearchViewModel

@Composable
fun PlaylistHost() {
    val navController = rememberNavController()
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val navigateToSearch = { navController.navigate(Screen.Search.route) }
    val navigateToSettings = { navController.navigate(Screen.Settings.route) }
    val navigateToPlaylists = { navController.navigate(Screen.Playlists.route) }
    val navigateToFavorites = { navController.navigate(Screen.Favorites.route) }

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
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.getViewModelFactory()
            )
            SearchScreen(
                onBackClick = navigateBack,
                viewModel = searchViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = navigateBack
            )
        }

        // Маршрут для экрана Плейлистов
        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                onBackClick = navigateBack
            )
        }

        // Маршрут для экрана Избранного
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackClick = navigateBack
            )
        }
    }
}