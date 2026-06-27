package com.example.playlistmaker.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.dependencyobject.DependencyContainer
import com.example.playlistmaker.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.playlistmaker.data.models.AudioTrack

class FavoritesViewModel(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : ViewModel() {

    private val _favoriteTracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val favoriteTracks: StateFlow<List<AudioTrack>> = _favoriteTracks

    init {
        viewModelScope.launch {
            favoriteTracksRepository.getFavoriteTracks().collect { tracks ->
                _favoriteTracks.value = tracks
            }
        }
    }

    fun toggleFavorite(track: AudioTrack, isFavorite: Boolean) {
        viewModelScope.launch {
            favoriteTracksRepository.setTrackIsFavorite(track, isFavorite)
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val repository = DependencyContainer.fetchFavoriteTracksRepository(app)

                FavoritesViewModel(repository)
            }
        }
    }
}