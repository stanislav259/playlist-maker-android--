package com.example.playlistmaker

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.playlistmaker.data.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.DbTracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.preferences.SearchHistoryPreferences
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.domain.SearchHistoryRepository

private val Context.dataStore by preferencesDataStore(name = "search_history_pref")

object Creator {

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun getDbTracksRepository(context: Context): TracksRepository {
        return DbTracksRepositoryImpl(AppDatabase.getDatabase(context))
    }

    fun getPlaylistsRepository(context: Context): PlaylistsRepository {
        return PlaylistsRepositoryImpl(AppDatabase.getDatabase(context))
    }

    fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val searchHistoryPreferences = SearchHistoryPreferences(context.dataStore)
        return SearchHistoryRepositoryImpl(searchHistoryPreferences)
    }
}