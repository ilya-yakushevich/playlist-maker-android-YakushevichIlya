package com.example.playlistmaker.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.data.models.AudioTrack
import com.example.playlistmaker.data.models.MusicPlaylist
import com.example.playlistmaker.dependencyobject.DependencyContainer
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsRepository: PlaylistsRepository,
                         private val tracksRepository: TracksRepository) : ViewModel() {

    val playlists: Flow<List<MusicPlaylist>> = playlistsRepository.getAllPlaylists()

    fun insertTrackToPlaylist(track: AudioTrack, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertTrackToPlaylist(track, playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(playlistId)
            playlistsRepository.deletePlaylistById(playlistId)
        }
    }

    fun toggleFavorite(track: AudioTrack, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
        }
    }

    fun isTrackFavorite(track: AudioTrack): Flow<Boolean> {
        return tracksRepository.getTrackByNameAndArtist(track)
            .map { databaseTrack ->
                databaseTrack?.favorite ?: false
            }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                PlaylistsViewModel(
                    playlistsRepository = DependencyContainer.fetchPlaylistsRepository(app),
                    tracksRepository = DependencyContainer.fetchTracksRepository(app)
                )
            }
        }
    }
}