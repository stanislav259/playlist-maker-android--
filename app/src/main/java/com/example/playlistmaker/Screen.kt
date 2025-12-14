package com.example.playlistmaker

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object Search : Screen("search_screen")
    object Settings : Screen("settings_screen")
    // object Playlists : Screen("playlists_screen")
    // object Favorites : Screen("favorites_screen")
}