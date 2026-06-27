package com.example.playlistmaker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CollectionDbModel::class, SongDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun songDao(): SongDao
}