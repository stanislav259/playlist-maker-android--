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
import kotlinx.coroutines.flow.asStateFlow


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

    val favoriteList: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    private val _coverImageUri = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val coverImageUri = _coverImageUri.asStateFlow()

    fun setCoverImageUri(uri: String?) {
        _coverImageUri.value = uri
    }

    fun createNewPlayList(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(
                name = namePlaylist,
                description = description,
                coverImageUri = _coverImageUri.value // Сохраняем выбранную обложку
            )
            _coverImageUri.value = null // Сбрасываем выбранную обложку после сохранения
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

    fun mergePlaylists(sourcePlaylist: Playlist, targetPlaylist: Playlist, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            sourcePlaylist.tracks.forEach { track ->
                tracksRepository.insertTrackToPlaylist(track, targetPlaylist.id)
            }
            playlistsRepository.deletePlaylistById(sourcePlaylist.id)

            viewModelScope.launch(Dispatchers.Main) {
                onComplete()
            }
        }
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