package com.example.playlistmaker.domain

import com.example.playlistmaker.data.database.MusicDatabase
import com.example.playlistmaker.data.database.CollectionDbModel // Замени на актуальное имя Entity плейлиста
import com.example.playlistmaker.data.database.* // Импорт всех мапперов (mapToDomain, mapToDbModel)
import com.example.playlistmaker.data.models.SearchQuery
import com.example.playlistmaker.data.models.NetworkResult // Наш базовый открытый класс ответа
import com.example.playlistmaker.data.models.AudioTrack
import com.example.playlistmaker.data.models.MusicPlaylist
import com.example.playlistmaker.domain.SearchHistoryPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

// ==========================================
// 1. ИНТЕРФЕЙСЫ СЛОЯ DOMAIN (КОНТРАКТЫ)
// ==========================================

interface FavoriteTracksRepository {
    fun getFavoriteTracks(): Flow<List<AudioTrack>>
    suspend fun setTrackIsFavorite(track: AudioTrack, isFavorite: Boolean)
}

interface PlaylistsRepository {
    fun getPlaylist(playlistId: Long): Flow<MusicPlaylist?>
    fun getAllPlaylists(): Flow<List<MusicPlaylist>>
    suspend fun addNewPlaylist(name: String, description: String, coverUri: String?)
    suspend fun deletePlaylistById(id: Long)
}

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<AudioTrack>
    fun getTrackByNameAndArtist(track: AudioTrack): Flow<AudioTrack?>
    fun getFavoriteTracks(): Flow<List<AudioTrack>>
    suspend fun insertTrackToPlaylist(track: AudioTrack, playlistId: Long)
    suspend fun deleteTrackFromPlaylist(track: AudioTrack)
    suspend fun updateTrackFavoriteStatus(track: AudioTrack, isFavorite: Boolean)
    suspend fun deleteTracksByPlaylistId(playlistId: Long)
}

interface SearchHistoryRepository {
    fun addSearchQuery(query: String)
    suspend fun getSearchHistory(): List<String>
}


// ==========================================
// 2. РЕАЛИЗАЦИИ РЕПОЗИТОРИЕВ (СЛОЙ DATA)
// ==========================================

class PlaylistsRepositoryImpl(
    private val database: MusicDatabase
) : PlaylistsRepository {

    private val playlistDao = database.collectionDao()
    private val songDao = database.songDao()

    override fun getPlaylist(playlistId: Long): Flow<MusicPlaylist?> {
        val currentPlaylistFlow = playlistDao.observeCollectionById(playlistId)
        val linkedTracksFlow = songDao.observeSongsForCollection(playlistId)

        return currentPlaylistFlow.combine(linkedTracksFlow) { playlistEntity, trackEntities ->
            playlistEntity?.let {
                MusicPlaylist(
                    id = it.collectionId,
                    name = it.title,
                    description = it.details ?: "",
                    coverUri = it.imagePath,
                    tracks = trackEntities.map { entity -> entity.mapToDomain() }
                )
            }
        }
    }

    override fun getAllPlaylists(): Flow<List<MusicPlaylist>> {
        return playlistDao.observeAllCollections().flatMapLatest { collectionEntities ->
            if (collectionEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flowOfPlaylists: List<Flow<MusicPlaylist>> = collectionEntities.map { entity ->
                    songDao.observeSongsForCollection(entity.collectionId).map { associatedTracks ->
                        MusicPlaylist(
                            id = entity.collectionId,
                            name = entity.title,
                            description = entity.details ?: "",
                            coverUri = entity.imagePath,
                            tracks = associatedTracks.map { it.mapToDomain() }
                        )
                    }
                }
                combine(flowOfPlaylists) { array -> array.toList() }
            }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverUri: String?) {
        val newCollection = CollectionDbModel(
            title = name,
            details = description,
            imagePath = coverUri
        )
        playlistDao.saveCollection(newCollection)
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.removeCollection(id)
    }
}


class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val database: MusicDatabase
) : TracksRepository {

    private val songDao = database.songDao()

    override suspend fun searchTracks(expression: String): List<AudioTrack> {
        val executionResult = networkClient.doRequest(SearchQuery(expression))

        // Проверяем, что запрос успешный (код 200)
        if (executionResult.responseStatus == 200 && executionResult is com.example.playlistmaker.data.models.SongsApiResponse) {
            // Преобразуем полученные из сети данные (RemoteModel) в доменные модели (AudioTrack)
            return executionResult.items.map { remoteModel ->
                AudioTrack(
                    trackName = remoteModel.title ?: "",
                    artistName = remoteModel.author ?: "",
                    trackTime  = remoteModel.durationMs ?: 0,
                    artworkUrl100 = remoteModel.coverPath ?: ""
                )
            }
        } else {
            // Если ошибка или статус не 200, возвращаем пустой список
            return emptyList()
        }
    }

    override suspend fun insertTrackToPlaylist(track: AudioTrack, playlistId: Long) {
        val songEntity = track.mapToDbModel().copy(linkedCollectionId = playlistId)
        songDao.saveSong(songEntity)
    }

    override suspend fun deleteTrackFromPlaylist(track: AudioTrack) {
        val independentEntity = track.mapToDbModel().copy(linkedCollectionId = null)
        songDao.saveSong(independentEntity)
    }

    override suspend fun updateTrackFavoriteStatus(track: AudioTrack, isFavorite: Boolean) {
        val existingRecord = songDao.fetchSongOnce(track.trackName, track.artistName)
        val freshEntity = track.mapToDbModel()

        val updatedRecord = if (existingRecord != null) {
            existingRecord.copy(isLiked = isFavorite)
        } else {
            freshEntity.copy(songId = 0L, isLiked = isFavorite)
        }
        songDao.saveSong(updatedRecord)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        songDao.clearSongsByCollection(playlistId)
    }

    override fun getFavoriteTracks(): Flow<List<AudioTrack>> {
        val favoritesFlow = songDao.observeLikedSongs()
        return favoritesFlow.map { entitiesList ->
            entitiesList.map { it.mapToDomain() }
        }
    }

    override fun getTrackByNameAndArtist(track: AudioTrack): Flow<AudioTrack?> {
        val singleTrackFlow = songDao.observeSong(track.trackName, track.artistName)
        return singleTrackFlow.map { entity ->
            entity?.mapToDomain()
        }
    }
}


class SearchHistoryRepositoryImpl(
    private val searchHistoryPreferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun addSearchQuery(query: String) {
        searchHistoryPreferences.addEntry(query)
    }

    override suspend fun getSearchHistory(): List<String> {
        val savedQueries = searchHistoryPreferences.getEntries()
        return savedQueries
    }
}


class FavoriteTracksRepositoryImpl(
    private val database: MusicDatabase
) : FavoriteTracksRepository {

    private val songDao = database.songDao()

    override fun getFavoriteTracks(): Flow<List<AudioTrack>> {
        val favoriteItemsFlow = songDao.observeLikedSongs()
        return favoriteItemsFlow.map { entityList ->
            entityList.map { it.mapToDomain() }
        }
    }

    override suspend fun setTrackIsFavorite(track: AudioTrack, isFavorite: Boolean) {
        val databaseRecord = songDao.fetchSongOnce(track.trackName, track.artistName)
        val baseEntity = track.mapToDbModel()

        val itemToSave = if (databaseRecord != null) {
            databaseRecord.copy(isLiked = isFavorite)
        } else {
            baseEntity.copy(songId = 0L, isLiked = isFavorite)
        }
        songDao.saveSong(itemToSave)
    }
}