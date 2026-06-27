package com.example.playlistmaker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme
import com.example.playlistmaker.R

class MainActivity : ComponentActivity() {
    private val searchViewModel by viewModels<SearchViewModel> {
        SearchViewModel.getViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaylistMakerTheme {
                PlaylistHost(searchViewModel = searchViewModel)
            }
        }
    }
}


@Composable
fun MainScreen(
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val blueBackground = Color(0xFF3772E7)
    val whiteBackground = Color(0xFFFFFFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(blueBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(start = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Playlist Maker",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = whiteBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(whiteBackground)
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            MainMenuItem(
                title = "Поиск",
                icon = Icons.Default.Search,
                onClick = onNavigateToSearch
            )
            MainMenuItem(
                title = "Плейлисты",
                icon = painterResource(id = R.drawable.playlists),
                onClick = onNavigateToPlaylists
            )
            MainMenuItem(
                title = "Избранное",
                icon = Icons.Default.FavoriteBorder,
                onClick = onNavigateToFavorites
            )
            MainMenuItem(
                title = "Настройки",
                icon = Icons.Default.Settings,
                onClick = onNavigateToSettings
            )
        }
    }
}

@Composable
fun MainMenuItem(title: String, icon: Any, onClick: () -> Unit) {
    val blackText = Color(0xFF1A1B22)
    val grayArrow = Color(0xFFAEAFB4)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon is ImageVector) {
                Icon(imageVector = icon, contentDescription = null, tint = blackText, modifier = Modifier.size(24.dp))
            } else if (icon is Painter) {
                Icon(painter = icon, contentDescription = null, tint = blackText, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = blackText)
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = grayArrow, modifier = Modifier.size(24.dp))
    }
}