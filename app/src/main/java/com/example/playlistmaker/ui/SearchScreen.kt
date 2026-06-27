package com.example.playlistmaker.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.data.models.AudioTrack

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: SearchViewModel,
    onTrackClick: (AudioTrack) -> Unit
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState(initial = emptyList())

    var query by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(query) {
        if (query.isEmpty()) {
            viewModel.clearSearch()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color(0xFF1A1B22))
            }
            Text("Поиск", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1B22))
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        ) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color(0xFF1A1B22),
                    fontSize = 16.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                ),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(Color(0xFF3F8AE0)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (query.isNotBlank()) {
                        viewModel.search(query)
                        keyboardController?.hide()
                    }
                }),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color(0xFFE6E8EB),
                                shape = if (screenState is SearchState.Initial && query.isEmpty() && searchHistory.isNotEmpty()) {
                                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                } else {
                                    RoundedCornerShape(8.dp)
                                }
                            )
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Поиск",
                            tint = Color(0xFFAEAFB4),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    if (query.isNotBlank()) {
                                        viewModel.search(query)
                                        keyboardController?.hide()
                                    }
                                }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(modifier = Modifier.weight(1f)) {
                            if (query.isEmpty()) {
                                Text(
                                    text = "Поиск",
                                    color = Color(0xFFAEAFB4),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    query = ""
                                    viewModel.clearSearch()
                                    keyboardController?.hide()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Очистить",
                                    tint = Color(0xFFAEAFB4),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            )

            if (screenState is SearchState.Initial && query.isEmpty() && searchHistory.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE6E8EB))
                        .padding(horizontal = 10.dp),
                    thickness = 2.dp,
                    color = Color(0xFFAEAFB4)
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    searchHistory.take(10).forEach { queryText ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(41.dp)
                                .background(Color(0xFFE6E8EB))
                                .clickable {
                                    query = queryText
                                    viewModel.search(queryText)
                                    keyboardController?.hide()
                                }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_history),
                                contentDescription = null,
                                tint = Color(0xFFAEAFB4),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = queryText,
                                fontSize = 16.sp,
                                color = Color(0xFF1A1B22),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        when (val state = screenState) {
            is SearchState.Initial -> {}
            is SearchState.Searching -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3772E7))
            }
            is SearchState.Empty -> {
                SearchPlaceholder(
                    iconResId = R.drawable.search_placeholder,
                    message = "Ничего не нашлось"
                )
            }
            is SearchState.Success -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(items = state.foundList) { track ->
                        TrackListItem(track = track, onClick = { onTrackClick(track) })
                    }
                }
            }
            is SearchState.Fail -> {
                SearchPlaceholder(
                    iconResId = R.drawable.connection_error,
                    message = "Проблемы со связью\nЗагрузка не удалась. Проверьте подключение к интернету",
                    onRetry = { viewModel.retry() }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackListItem(
    track: AudioTrack,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
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
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = track.trackName,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_music),
            error = painterResource(id = R.drawable.ic_music)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(
                text = track.trackName,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${track.artistName} • ${track.trackTime}",
                    fontSize = 11.sp,
                    color = Color(0xFFAEAFB4),
                    maxLines = 1
                )
            }
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFAEAFB4)
        )
    }
}

@Composable
fun SearchPlaceholder(
    iconResId: Int,
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 19.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(312.dp)
        )
        if (onRetry != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
            }
        }
    }
}