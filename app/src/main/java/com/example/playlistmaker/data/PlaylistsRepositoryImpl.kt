package com.example.playlistmaker.data

import com.example.playlistmaker.Playlist
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val database: AppDatabase
) : PlaylistsRepository {

    private val playlistDao = database.playlistDao()
    private val trackDao = database.trackDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylist(playlistId).combine(trackDao.getTracksForPlaylist(playlistId)) { playlistEntity, trackEntities ->
            if (playlistEntity != null) {
                Playlist(
                    id = playlistEntity.id,
                    name = playlistEntity.name,
                    description = playlistEntity.description,
                    coverImageUri = playlistEntity.coverImageUri, // Передаем обложку
                    tracks = trackEntities.map { entity ->
                        Track(
                            id = entity.id,
                            trackName = entity.trackName,
                            artistName = entity.artistName,
                            trackTime = entity.trackTime,
                            artworkUrl100 = entity.artworkUrl100,
                            playlistId = entity.playlistId,
                            favorite = entity.favorite
                        )
                    }
                )
            } else {
                null
            }
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        playlistDao.getAllPlaylists().collect { playlists ->
            val domainPlaylists = playlists.map { entity ->
                val trackEntities = trackDao.getTracksForPlaylistDirect(entity.id)
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    coverImageUri = entity.coverImageUri,
                    tracks = trackEntities.map { trackEntity ->
                        Track(
                            id = trackEntity.id,
                            trackName = trackEntity.trackName,
                            artistName = trackEntity.artistName,
                            trackTime = trackEntity.trackTime,
                            artworkUrl100 = trackEntity.artworkUrl100,
                            playlistId = trackEntity.playlistId,
                            favorite = trackEntity.favorite
                        )
                    }
                )
            }
            emit(domainPlaylists)
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverImageUri: String?) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = coverImageUri // Сохраняем обложку
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.deletePlaylist(id)
    }
}