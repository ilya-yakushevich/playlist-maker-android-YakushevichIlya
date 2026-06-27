package com.example.playlistmaker.ui

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.MusicPlaylist

@Composable
fun PlaylistsScreen(
    playlistsViewModel: PlaylistsViewModel,
    onBackClick: () -> Unit,
    onNavigateToPlaylistDetails: (Long) -> Unit,
    onNavigateToCreatePlaylist: () -> Unit
) {
    val playlists by playlistsViewModel.playlists.collectAsState(initial = emptyList())
    var playlistToDelete by remember { mutableStateOf<MusicPlaylist?>(null) }
    val whiteBackground = Color(0xFFFFFFFF)
    val blackText = Color(0xFF1A1B22)
    val fabBackground = Color(0x405F5F6E)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = whiteBackground,
        floatingActionButton = {
            IconButton(
                onClick = onNavigateToCreatePlaylist,
                modifier = Modifier
                    .size(51.dp)
                    .background(fabBackground, CircleShape)

            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить плейлист",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = blackText)
                }
                Text(
                    text = "Плейлисты",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = blackText
                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(playlists) { playlist ->
                    PlaylistListItem(
                        playlist = playlist,
                        onClick = { onNavigateToPlaylistDetails(playlist.id) },
                        onLongClick = { playlistToDelete = playlist }
                    )
                }
            }

            playlistToDelete?.let { playlist ->
                AlertDialog(
                    onDismissRequest = { playlistToDelete = null },
                    title = { Text("Удаление плейлиста") },
                    text = { Text("Вы уверены, что хотите удалить плейлист «${playlist.name}»?") },
                    confirmButton = {
                        TextButton(onClick = {
                            playlistsViewModel.deletePlaylist(playlist.id)
                            playlistToDelete = null
                        }) { Text("Удалить") }
                    },
                    dismissButton = {
                        TextButton(onClick = { playlistToDelete = null }) { Text("Отмена") }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistListItem(playlist: MusicPlaylist, onClick: () -> Unit, onLongClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(61.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!playlist.coverUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(playlist.coverUri),
                    contentDescription = "Обложка плейлиста",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_playlist_placeholder),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "${playlist.tracks.size} треков",
                fontSize = 11.sp,
                color = Color(0xFFAEAFB4),
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}