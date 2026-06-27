package com.example.playlistmaker.data.network

import com.example.playlistmaker.domain.NetworkClient
import com.example.playlistmaker.data.models.NetworkResult
import com.example.playlistmaker.data.models.SearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkProvider(
    private val apiService: MusicSearchApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): NetworkResult {
        return withContext(Dispatchers.IO) {
            when (dto) {
                is SearchQuery -> {
                    try {
                        val result = apiService.fetchSongsByQuery(dto.queryText)
                        result.apply { responseStatus = 200 }
                    } catch (error: Exception) {
                        error.printStackTrace()
                        NetworkResult().apply { responseStatus = 500 }
                    }
                }
                else -> {
                    NetworkResult().apply { responseStatus = 400 }
                }
            }
        }
    }
}