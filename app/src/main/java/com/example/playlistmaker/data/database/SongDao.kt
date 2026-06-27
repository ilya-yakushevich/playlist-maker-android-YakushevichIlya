package com.example.playlistmaker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("select * from saved_songs_table where song_title = :title and author = :authorName limit 1")
    fun observeSong(title: String, authorName: String): Flow<SongDbModel?>

    @Query("select * from saved_songs_table where song_title = :title and author = :authorName limit 1")
    suspend fun fetchSongOnce(title: String, authorName: String): SongDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSong(song: SongDbModel)

    @Query("select * from saved_songs_table where is_liked = 1")
    fun observeLikedSongs(): Flow<List<SongDbModel>>

    @Query("delete from saved_songs_table where linked_collection_id = :colId")
    suspend fun clearSongsByCollection(colId: Long)

    @Query("select * from saved_songs_table where linked_collection_id = :colId")
    fun observeSongsForCollection(colId: Long): Flow<List<SongDbModel>>
}