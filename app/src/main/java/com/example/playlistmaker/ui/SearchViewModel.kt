package com.example.playlistmaker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.playlistmaker.dependencyobject.DependencyContainer
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val tracksRepository: TracksRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory = _searchHistory.asStateFlow()

    private var lastQuery: String = ""

    init {
        refreshHistory()
    }

    fun search(whatSearch: String) {
        lastQuery = whatSearch
        if (whatSearch.isBlank()) {
            _searchScreenState.update { SearchState.Initial }
            return
        }

        viewModelScope.launch {
            _searchScreenState.update { SearchState.Searching }

            try {
                val list = withContext(Dispatchers.IO) {
                    tracksRepository.searchTracks(expression = whatSearch)
                }
                _searchScreenState.update {
                    if (list.isEmpty()) {
                        SearchState.Empty
                    } else {
                        saveToHistory(whatSearch)
                        SearchState.Success(foundList = list)
                    }
                }
            } catch (e: Exception) {
                _searchScreenState.update {
                    SearchState.Fail("Ошибка сети: проверьте подключение")
                }
            }
        }
    }

    private fun saveToHistory(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.addSearchQuery(query)
        }
    }
    fun refreshHistory() {
        viewModelScope.launch {
            val history = searchHistoryRepository.getSearchHistory()
            _searchHistory.update { history }
        }
    }
    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryRepository.addSearchQuery("")
            _searchHistory.update { emptyList() }
        }
    }

    fun retry() {
        search(lastQuery)
    }

    fun clearSearch() {
        _searchScreenState.update { SearchState.Initial }
        refreshHistory()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                    val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                    return SearchViewModel(
                        tracksRepository = DependencyContainer.fetchTracksRepository(application),
                        searchHistoryRepository = DependencyContainer.fetchSearchHistoryRepository(application)
                    ) as T
                }
            }
    }
}