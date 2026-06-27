package com.example.playlistmaker.dependencyobject
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room

// Импорты наших новых классов из слоя data
import com.example.playlistmaker.data.database.MusicDatabase
import com.example.playlistmaker.data.network.MusicSearchApiService
import com.example.playlistmaker.data.network.RetrofitNetworkProvider

// Импорты слоя domain (пока со старыми названиями)
import com.example.playlistmaker.domain.FavoriteTracksRepository
import com.example.playlistmaker.domain.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.domain.PlaylistsRepository
import com.example.playlistmaker.domain.PlaylistsRepositoryImpl
import com.example.playlistmaker.domain.SearchHistoryPreferences
import com.example.playlistmaker.domain.SearchHistoryRepository
import com.example.playlistmaker.domain.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.TracksRepository
import com.example.playlistmaker.domain.TracksRepositoryImpl

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val Context.historyStore: DataStore<Preferences> by preferencesDataStore(name = "user_search_history_settings")

object DependencyContainer {

    @Volatile
    private var localDatabase: MusicDatabase? = null

    @Volatile
    private var historyPrefsInstance: SearchHistoryPreferences? = null

    fun provideDatabase(ctx: Context): MusicDatabase {
        return localDatabase ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                ctx.applicationContext,
                MusicDatabase::class.java,
                "music_flow_storage.db"
            ).build()
            localDatabase = instance
            instance
        }
    }

    private val networkProvider: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://itunes.apple.com")
            .build()
    }

    private val searchService: MusicSearchApiService by lazy {
        networkProvider.create(MusicSearchApiService::class.java)
    }

    fun fetchTracksRepository(context: Context): TracksRepository {
        val db = provideDatabase(context)
        val apiClient = RetrofitNetworkProvider(searchService)
        return TracksRepositoryImpl(apiClient, db)
    }

    fun fetchPlaylistsRepository(context: Context): PlaylistsRepository {
        val db = provideDatabase(context)
        return PlaylistsRepositoryImpl(db)
    }

    fun fetchFavoriteTracksRepository(context: Context): FavoriteTracksRepository {
        val db = provideDatabase(context)
        return FavoriteTracksRepositoryImpl(db)
    }

    private fun resolveSearchPreferences(context: Context): SearchHistoryPreferences {
        return historyPrefsInstance ?: synchronized(this) {
            val instance = SearchHistoryPreferences(context.applicationContext.historyStore)
            historyPrefsInstance = instance
            instance
        }
    }

    fun fetchSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val preferences = resolveSearchPreferences(context)
        return SearchHistoryRepositoryImpl(searchHistoryPreferences = preferences)
    }
}