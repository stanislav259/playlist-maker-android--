package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.domain.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)

    override suspend fun doRequest(dto: Any): BaseResponse {
        if (dto !is TracksSearchRequest) {
            return BaseResponse().apply { resultCode = 400 }
        }
        return try {
            val response = iTunesService.search(dto.expression)
            response.apply { resultCode = 200 }
        } catch (e: Exception) {
            BaseResponse().apply { resultCode = -1 } // Код ошибки сети
        }
    }
}