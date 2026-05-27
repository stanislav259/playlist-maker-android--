package com.example.playlistmaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.PlaylistsRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow

class PlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val playlistId: Long
) : ViewModel() {

    val playlist: Flow<Playlist?> = playlistsRepository.getPlaylist(playlistId)

    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        fun getViewModelFactory(playlistId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistViewModel(
                        playlistsRepository = PlaylistsRepositoryImpl(scope = GlobalScope),
                        playlistId = playlistId
                    ) as T
                }
            }
    }
}