package com.example.playlistmaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.AudioTrack

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onBackClick: () -> Unit,
    onTrackClick: (AudioTrack) -> Unit
) {
    val favoriteList by viewModel.favoriteTracks.collectAsState(initial = emptyList())
    val ypWhite = Color(0xFFFFFFFF)
    val ypBlack = Color(0xFF1A1B22)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ypWhite)
    ) {
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
                    tint = ypBlack
                )
            }
            Text(
                text = "Избранные треки",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = ypBlack,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        if (favoriteList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ваша медиатека пуста",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Normal,
                    color = ypBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(312.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(favoriteList.size) { index ->
                    val track = favoriteList[index]

                    TrackListItem(
                        track = track,
                        onClick = {
                            onTrackClick(track)
                        },
                        onLongClick = {
                            viewModel.toggleFavorite(track, false)
                        }
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color(0xFFE6E8EB)
                    )
                }
            }
        }
    }
}