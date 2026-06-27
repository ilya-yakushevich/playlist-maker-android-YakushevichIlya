package com.example.playlistmaker.data.models

data class AudioTrack(
    val id: Long = 0L,
    val trackName: String,
    val artistName: String,
    val trackTime: Int,
    val artworkUrl100: String,
    val playlistId: Long = 0L,
    val favorite: Boolean = false
)