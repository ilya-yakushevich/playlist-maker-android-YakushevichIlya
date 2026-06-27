package com.example.playlistmaker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_songs_table")
data class SongDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_id") val songId: Long = 0,

    @ColumnInfo(name = "song_title") val songTitle: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "duration") val duration: Int,
    @ColumnInfo(name = "cover_url") val coverUrl: String,
    @ColumnInfo(name = "linked_collection_id") val linkedCollectionId: Long?,
    @ColumnInfo(name = "is_liked") val isLiked: Boolean
)