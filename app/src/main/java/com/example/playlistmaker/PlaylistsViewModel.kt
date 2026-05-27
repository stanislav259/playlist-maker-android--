package com.example.playlistmaker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.DatabaseMock
import com.example.playlistmaker.data.DatabaseMockProvider
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository,
    private val databaseRepository: DatabaseMock
) : ViewModel() {

    val playlists: Flow<List<Playlist>> = flow {
        playlistsRepository.getAllPlaylists().collect { list ->
            emit(list)
        }
    }

    val favoriteList: Flow<List<Track>> = databaseRepository.getFavoriteTracks()

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description)
        }
    }

    suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        tracksRepository.insertTrackToPlaylist(track, playlistId)
    }

    suspend fun toggleFavorite(track: Track, isFavorite: Boolean) {
        tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
    }

    suspend fun deleteTrackFromPlaylist(track: Track) {
        tracksRepository.deleteTrackFromPlaylist(track)
    }

    suspend fun deletePlaylistById(id: Long) {
        tracksRepository.deleteTracksByPlaylistId(id)
        playlistsRepository.deletePlaylistById(id)
    }

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track = track).firstOrNull()
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistsViewModel(
                        playlistsRepository = Creator.getPlaylistsRepository(context),
                        tracksRepository = Creator.getDbTracksRepository(context),
                        databaseRepository = DatabaseMockProvider.getDatabase(kotlinx.coroutines.GlobalScope)
                    ) as T
                }
            }
    }
}