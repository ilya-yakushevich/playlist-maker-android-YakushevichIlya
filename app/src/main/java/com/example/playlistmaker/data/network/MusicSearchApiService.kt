package com.example.playlistmaker.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.playlistmaker.data.models.SongsApiResponse

interface MusicSearchApiService {
    @GET("/search?entity=song")
    suspend fun fetchSongsByQuery(@Query("term") searchQuery: String): SongsApiResponse
}