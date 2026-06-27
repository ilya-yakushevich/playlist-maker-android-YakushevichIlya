package com.example.playlistmaker.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_collections_table")
data class CollectionDbModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "collection_id") val collectionId: Long = 0,

    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "details") val details: String?,
    @ColumnInfo(name = "image_path") val imagePath: String? = null
)