package com.example.playlistmaker.ui

import com.example.playlistmaker.data.models.AudioTrack
sealed class SearchState {
    object Initial : SearchState()
    object Searching : SearchState()
    object Empty : SearchState()
    data class Success(val foundList: List<AudioTrack>) : SearchState()
    data class Fail(val error: String) : SearchState()
}