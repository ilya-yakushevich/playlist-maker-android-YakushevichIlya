package com.example.playlistmaker.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.dependencyobject.DependencyContainer
import com.example.playlistmaker.domain.PlaylistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(
    private val application: Application,
    private val playlistsRepository: PlaylistsRepository
) : ViewModel() {

    private val _coverImageUri = MutableStateFlow<String?>(null)
    val coverImageUri: StateFlow<String?> = _coverImageUri.asStateFlow()

    fun setCoverImageUri(uri: String?) {
        _coverImageUri.value = uri
    }

    private fun saveImageToInternalStorage(uriString: String?): String? {
        if (uriString.isNullOrBlank()) return null

        return try {
            val sourceUri = Uri.parse(uriString)

            val filePath = File(application.filesDir, "playlist_covers")
            if (!filePath.exists()) {
                filePath.mkdirs()
            }
            val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

            application.contentResolver.openInputStream(sourceUri).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun createNewPlaylist(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val permanentPath = saveImageToInternalStorage(_coverImageUri.value)

            playlistsRepository.addNewPlaylist(
                name = name,
                description = description,
                coverUri = permanentPath
            )
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val repository = DependencyContainer.fetchPlaylistsRepository(app)
                NewPlaylistViewModel(app, repository)
            }
        }
    }
}