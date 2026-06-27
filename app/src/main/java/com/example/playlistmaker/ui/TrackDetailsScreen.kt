package com.example.playlistmaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.AudioTrack
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    track: AudioTrack,
    playlistsViewModel: PlaylistsViewModel,
    onBackClick: () -> Unit
) {
    val playlists by playlistsViewModel.playlists.collectAsState(initial = emptyList())

    var isFavorite by remember(track.trackName, track.artistName) { mutableStateOf(track.favorite) }

    LaunchedEffect(track.trackName, track.artistName) {
        playlistsViewModel.isTrackFavorite(track).collect { favoriteFromDb ->
            isFavorite = favoriteFromDb
        }
    }

    var showPlaylistSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val ypBlack = Color(0xFF1A1B22)
    val ypTextGray = Color(0xFFAEAFB4)
    val ypButtonBackground = Color(0x401A1B22)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 56.dp, bottom = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(44.dp))

                    val bigArtworkUrl = remember(track.artworkUrl100) { getCoverArtwork(track.artworkUrl100) }
                    val placeholderPainter = painterResource(id = R.drawable.ic_playlist_placeholder)

                    AsyncImage(
                        model = bigArtworkUrl,
                        contentDescription = "Обложка трека",
                        placeholder = placeholderPainter,
                        error = placeholderPainter,
                        fallback = placeholderPainter,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(312.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xE6E6E6))
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = track.trackName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = ypBlack,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = track.artistName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = ypBlack,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(38.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showPlaylistSheet = true },
                            modifier = Modifier
                                .size(51.dp)
                                .background(ypButtonBackground, RoundedCornerShape(69.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add),
                                contentDescription = "Добавить в плейлист",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = { playlistsViewModel.toggleFavorite(track, !isFavorite) },
                            modifier = Modifier
                                .size(51.dp)
                                .background(ypButtonBackground, RoundedCornerShape(69.dp))
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Избранное",
                                tint = if (isFavorite) Color.Red else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Длительность", fontSize = 13.sp, color = ypTextGray)
                        Text(
                            text = track.trackTime.toDurationString(),
                            fontSize = 13.sp,
                            color = ypBlack,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = ypBlack,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (showPlaylistSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPlaylistSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Text(
                        text = "Добавить в плейлист",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn {
                        items(items = playlists) { playlist ->
                            PlaylistListItem(
                                playlist = playlist,
                                onClick = {
                                    playlistsViewModel.insertTrackToPlaylist(track, playlist.id)
                                    showPlaylistSheet = false
                                },
                                onLongClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
fun Int.toDurationString(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}
private fun getCoverArtwork(url: String): String {
    return url.replaceAfterLast('/', "512x512bb.jpg")
}