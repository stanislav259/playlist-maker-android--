package com.example.playlistmaker.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.data.network.Track
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DbTracksRepositoryImpl(
    private val database: AppDatabase
) : TracksRepository {

    private val trackDao = database.trackDao()

    override suspend fun searchTracks(expression: String): List<Track> {
        return trackDao.searchTracks(expression).map { mapToTrack(it) }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return trackDao.getTrackByNameAndArtist(track.trackName, track.artistName).map {
            it?.let { mapToTrack(it) }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        trackDao.insertTrack(mapToEntity(track).copy(playlistId = playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        trackDao.insertTrack(mapToEntity(track).copy(playlistId = 0))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        trackDao.insertTrack(mapToEntity(track).copy(favorite = isFavorite))
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        trackDao.deleteTracksByPlaylistId(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { list ->
            list.map { mapToTrack(it) }
        }
    }

    private fun mapToTrack(entity: TrackEntity): Track {
        return Track(
            id = entity.id,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTime = entity.trackTime,
            artworkUrl100 = entity.artworkUrl100,
            playlistId = entity.playlistId,
            favorite = entity.favorite
        )
    }

    private fun mapToEntity(track: Track): TrackEntity {
        return TrackEntity(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            playlistId = track.playlistId,
            favorite = track.favorite
        )
    }
}