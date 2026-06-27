package com.example.playlistmaker.data.models

import com.google.gson.annotations.SerializedName

data class SongRemoteModel(
    @SerializedName("trackName")
    val title: String?,

    @SerializedName("artistName")
    val author: String?,

    @SerializedName("trackTimeMillis")
    val durationMs: Int?,

    @SerializedName("artworkUrl100")
    val coverPath: String?
)