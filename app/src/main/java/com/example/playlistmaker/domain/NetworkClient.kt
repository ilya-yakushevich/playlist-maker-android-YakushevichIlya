package com.example.playlistmaker.domain
import com.example.playlistmaker.data.models.NetworkResult
interface NetworkClient {
    suspend fun doRequest(dto: Any): NetworkResult
}