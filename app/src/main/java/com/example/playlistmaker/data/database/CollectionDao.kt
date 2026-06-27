package com.example.playlistmaker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCollection(model: CollectionDbModel)

    @Query("select * from user_collections_table")
    fun observeAllCollections(): Flow<List<CollectionDbModel>>

    @Query("select * from user_collections_table where collection_id = :reqId")
    fun observeCollectionById(reqId: Long): Flow<CollectionDbModel?>

    @Query("delete from user_collections_table where collection_id = :reqId")
    suspend fun removeCollection(reqId: Long)
}