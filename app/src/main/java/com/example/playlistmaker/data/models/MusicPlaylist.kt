package com.example.playlistmaker.data.models

data class MusicPlaylist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val coverUri: String? = null,
    val tracks: List<AudioTrack> = emptyList()
)