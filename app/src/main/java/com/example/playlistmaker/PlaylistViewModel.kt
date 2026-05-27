package com.example.playlistmaker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val playlistId: Long
) : ViewModel() {

    val playlist: Flow<Playlist?> = playlistsRepository.getPlaylist(playlistId)

    companion object {
        fun getViewModelFactory(context: Context, playlistId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistViewModel(
                        playlistsRepository = Creator.getPlaylistsRepository(context),
                        playlistId = playlistId
                    ) as T
                }
            }
    }
}