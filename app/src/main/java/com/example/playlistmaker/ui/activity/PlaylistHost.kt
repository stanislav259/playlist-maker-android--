package com.example.playlistmaker.ui.activity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.playlistmaker.Screen
import com.example.playlistmaker.SearchViewModel
import com.example.playlistmaker.PlaylistsViewModel
import com.example.playlistmaker.PlaylistViewModel
import com.example.playlistmaker.ui.activity.PlaylistsScreen
import com.example.playlistmaker.ui.activity.PlaylistScreen
import com.example.playlistmaker.ui.activity.CreatePlaylistScreen
import com.example.playlistmaker.ui.activity.FavoritesScreen
import com.example.playlistmaker.ui.activity.SettingsScreen
import com.example.playlistmaker.ui.activity.SearchScreen
import com.example.playlistmaker.ui.activity.MainScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun PlaylistHost() {
    val navController = rememberNavController()
    val playlistsViewModel: PlaylistsViewModel = viewModel()

    val navigateBack: () -> Unit = { navController.popBackStack() }
    val navigateToSearch = { navController.navigate(Screen.Search.route) }
    val navigateToSettings = { navController.navigate(Screen.Settings.route) }
    val navigateToPlaylists = { navController.navigate(Screen.Playlists.route) }
    val navigateToFavorites = { navController.navigate(Screen.Favorites.route) }
    val navigateToCreatePlaylist = { navController.navigate(Screen.CreatePlaylist.route) }

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
                onTrackClick = { track ->
                    navController.navigate(
                        Screen.TrackDetails.createRoute(track.trackName, track.artistName)
                    )
                },
                viewModel = searchViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = navigateBack
            )
        }

        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                modifier = Modifier,
                playlistsViewModel = playlistsViewModel,
                addNewPlaylist = navigateToCreatePlaylist,
                navigateToPlaylist = { playlistId ->
                    navController.navigate(Screen.PlaylistDetails.createRoute(playlistId))
                },
                navigateBack = navigateBack
            )
        }

        composable(Screen.CreatePlaylist.route) {
            CreatePlaylistScreen(
                onBackClick = navigateBack,
                viewModel = playlistsViewModel
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackClick = navigateBack
            )
        }

        composable(
            route = Screen.PlaylistDetails.route,
            arguments = listOf(
                navArgument("playlistId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L

            val playlistViewModel: PlaylistViewModel = viewModel(
                factory = PlaylistViewModel.getViewModelFactory(playlistId)
            )

            PlaylistScreen(
                viewModel = playlistViewModel,
                navigateToTrack = { track ->
                    navController.navigate(
                        Screen.TrackDetails.createRoute(track.trackName, track.artistName)
                    )
                },
                navigateBack = navigateBack
            )
        }

        composable(
            route = Screen.TrackDetails.route,
            arguments = listOf(
                navArgument("trackName") { type = NavType.StringType },
                navArgument("artistName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedTrackName = backStackEntry.arguments?.getString("trackName") ?: ""
            val encodedArtistName = backStackEntry.arguments?.getString("artistName") ?: ""

            val trackName = URLDecoder.decode(encodedTrackName, StandardCharsets.UTF_8.toString())
            val artistName = URLDecoder.decode(encodedArtistName, StandardCharsets.UTF_8.toString())

            TrackDetailsScreen(
                trackName = trackName,
                artistName = artistName,
                onBackClick = navigateBack,
                viewModel = playlistsViewModel
            )
        }
    }
}