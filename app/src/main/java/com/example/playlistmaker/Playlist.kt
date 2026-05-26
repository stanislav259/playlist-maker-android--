package com.example.playlistmaker

import com.example.playlistmaker.data.network.Track

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    var tracks: List<Track>
)