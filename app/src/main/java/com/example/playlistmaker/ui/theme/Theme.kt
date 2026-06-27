package com.example.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = YPBlue,
    onPrimary = YPWhite,
    background = YPWhite,
    surface = YPWhite,
    onSurface = YPBlack
)

@Composable
fun PlaylistMakerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}