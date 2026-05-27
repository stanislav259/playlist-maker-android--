package com.example.playlistmaker.data.network

data class Track(
    val id: Long = 0,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String = "",
    val playlistId: Long = 0,
    val favorite: Boolean = false
)