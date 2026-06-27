package com.example.playlistmaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.AudioTrack


@Composable
fun PlaylistScreen(
    index: Int,
    navigateBack: () -> Unit,
    onTrackClick: (AudioTrack) -> Unit
) {
    val viewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModel.getViewModelFactory(index.toLong())
    )
    val playlist by viewModel.playlist.collectAsState(null)
    val ypBlack = Color(0xFF1A1B22)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = navigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = ypBlack)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить плейлист", tint = ypBlack)
            }
        }

        playlist?.let { item ->
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                PlaylistCover(imageUrl = item.coverUri)
            }

            Text(
                text = item.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ypBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 60.dp)
            )

            if (item.description.isNotEmpty()) {
                Text(
                    text = item.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = ypBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                )
            }

            val totalMinutes = remember(item.tracks) { calculateTotalMinutes(item.tracks) }
            val minutesWord = remember(totalMinutes) { getMinutesWord(totalMinutes) }
            val tracksWord = remember(item.tracks.size) { getTracksWord(item.tracks.size) }

            Text(
                text = "$totalMinutes $minutesWord • ${item.tracks.size} $tracksWord",
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = ypBlack,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(item.tracks) { track ->
                    TrackListItem(track = track, onClick = { onTrackClick(track) })
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить плейлист") },
            text = { Text("Вы уверены, что хотите удалить плейлист «${playlist?.name}»?") },
            confirmButton = {
                TextButton(onClick = {
                    playlist?.let {
                        viewModel.deletePlaylist(it.id)
                        navigateBack()
                    }
                    showDeleteDialog = false
                }) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun PlaylistCover(imageUrl: String?) {
    val placeholderPainter = painterResource(id = R.drawable.ic_playlist_placeholder)

    val imageModel = remember(imageUrl) {
        if (!imageUrl.isNullOrBlank() && !imageUrl.startsWith("http")) {
            java.io.File(imageUrl)
        } else {
            imageUrl
        }
    }

    Box(
        modifier = Modifier.size(312.dp).clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageModel != null) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Обложка плейлиста",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = placeholderPainter,
                contentDescription = "Нет обложки",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

private fun calculateTotalMinutes(tracks: List<AudioTrack>): Int {
    val totalMillis = tracks.sumOf { it.trackTime }
    return (totalMillis + 59_999) / 60_000
}

private fun getMinutesWord(minutes: Int): String {
    val preLastDigit = minutes % 100 / 10
    if (preLastDigit == 1) return "минут"
    return when (minutes % 10) {
        1 -> "минута"
        2, 3, 4 -> "минуты"
        else -> "минут"
    }
}

private fun getTracksWord(count: Int): String {
    val preLastDigit = count % 100 / 10
    if (preLastDigit == 1) return "треков"
    return when (count % 10) {
        1 -> "трек"
        2, 3, 4 -> "трека"
        else -> "треков"
    }
}