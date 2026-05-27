package com.example.playlistmaker

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object Search : Screen("search_screen")
    object Settings : Screen("settings_screen")
    object Playlists : Screen("playlists_screen")
    object Favorites : Screen("favorites_screen")
    object CreatePlaylist : Screen("create_playlist_screen")

    object TrackDetails : Screen("track_details_screen/{trackName}/{artistName}") {
        fun createRoute(trackName: String, artistName: String): String {
            val encodedTrack = URLEncoder.encode(trackName, StandardCharsets.UTF_8.toString())
            val encodedArtist = URLEncoder.encode(artistName, StandardCharsets.UTF_8.toString())
            return "track_details_screen/$encodedTrack/$encodedArtist"
        }
    }
    object PlaylistDetails : Screen("playlist_details_screen/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist_details_screen/$playlistId"
    }
}