package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.NetworkClient

class RetrofitNetworkClient(private val storage: Storage) : NetworkClient {

    override fun doRequest(request: Any): TracksSearchResponse {
        val searchList = storage.search((request as TracksSearchRequest).expression)
        return TracksSearchResponse(searchList).apply { resultCode = 200 }
    }
}