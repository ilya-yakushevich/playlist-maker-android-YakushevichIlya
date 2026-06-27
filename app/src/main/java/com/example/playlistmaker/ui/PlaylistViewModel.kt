package com.example.playlistmaker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.data.models.MusicPlaylist
import com.example.playlistmaker.dependencyobject.DependencyContainer
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository,
    private val playlistId: Long
) : ViewModel() {

    val playlist: Flow<MusicPlaylist?> = playlistsRepository.getPlaylist(playlistId)

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(playlistId)
            playlistsRepository.deletePlaylistById(playlistId)
        }
    }

    companion object {
        fun getViewModelFactory(playlistId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                PlaylistViewModel(
                    playlistsRepository = DependencyContainer.fetchPlaylistsRepository(application),
                    tracksRepository = DependencyContainer.fetchTracksRepository(application),
                    playlistId = playlistId
                )
            }
        }
    }
}