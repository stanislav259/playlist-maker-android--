package com.example.playlistmaker.domain

interface SearchHistoryRepository {
    fun addSearchQuery(query: String)
    suspend fun getSearchHistory(): List<String>
}