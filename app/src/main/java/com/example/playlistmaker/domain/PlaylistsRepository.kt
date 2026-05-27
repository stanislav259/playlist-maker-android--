package com.example.playlistmaker.domain

import com.example.playlistmaker.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<Playlist?>
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addNewPlaylist(name: String, description: String, coverImageUri: String?)
    suspend fun deletePlaylistById(id: Long)
}