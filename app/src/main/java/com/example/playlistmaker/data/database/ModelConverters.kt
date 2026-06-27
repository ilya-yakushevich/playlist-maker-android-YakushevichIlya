package com.example.playlistmaker.data.database

import com.example.playlistmaker.data.models.SongRemoteModel
import  com.example.playlistmaker.data.models.AudioTrack
import  com.example.playlistmaker.data.models.MusicPlaylist
import java.util.Locale



fun CollectionDbModel.mapToDomain(): MusicPlaylist {
    return MusicPlaylist(
        id = this.collectionId,
        name = this.title,
        description = this.details ?: "",
        coverUri = this.imagePath,
        tracks = emptyList()
    )
}

fun SongDbModel.mapToDomain(): AudioTrack {
    return AudioTrack(
        id = this.songId,
        trackName = this.songTitle,
        artistName = this.author,
        trackTime = this.duration,
        artworkUrl100 = this.coverUrl,
        favorite = this.isLiked
    )
}

fun AudioTrack.mapToDbModel(): SongDbModel {
    return SongDbModel(
        songId = this.id,
        songTitle = this.trackName,
        author = this.artistName,
        duration = this.trackTime,
        coverUrl = this.artworkUrl100,
        linkedCollectionId = null,
        isLiked = this.favorite
    )
}

fun SongRemoteModel.mapToDomain(): AudioTrack {
    val titleString = this.title ?: "Неизвестный трек"
    val artistString = this.author ?: "Неизвестный исполнитель"

    return AudioTrack(
        trackName = titleString,
        artistName = artistString,
        trackTime = this.durationMs ?: 0,
        artworkUrl100 = this.coverPath ?: ""
    )
}
