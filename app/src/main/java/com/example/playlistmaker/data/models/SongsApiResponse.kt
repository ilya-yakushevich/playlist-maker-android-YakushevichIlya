package com.example.playlistmaker.data.models

import com.google.gson.annotations.SerializedName

class SongsApiResponse(
    @SerializedName("results")
    val items: List<SongRemoteModel>
) : NetworkResult()