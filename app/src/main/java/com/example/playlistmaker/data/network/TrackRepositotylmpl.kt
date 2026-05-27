package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                val seconds = (it.trackTimeMillis ?: 0) / 1000
                val minutes = seconds / 60
                val trackTime = "%02d".format(minutes) + ":" + "%02d".format(seconds - minutes * 60)

                val id = ((it.trackName ?: "") + (it.artistName ?: "")).hashCode().toLong()
                Track(
                    id = id,
                    trackName = it.trackName ?: "Неизвестный трек",
                    artistName = it.artistName ?: "Неизвестный исполнитель",
                    trackTime = trackTime,
                    artworkUrl100 = it.artworkUrl100 ?: ""
                )
            }
        } else if (response.resultCode == -1) {
            throw java.io.IOException("Проблемы со связью. Проверьте подключение к интернету.")
        } else {
            emptyList()
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> = emptyFlow()
    override fun getFavoriteTracks(): Flow<List<Track>> = emptyFlow()
    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {}
    override suspend fun deleteTrackFromPlaylist(track: Track) {}
    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {}
    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {}
}