package com.example.playlistmaker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.playlistmaker.data.models.AudioTrack

enum class PlaylistScreen {
    Main,
    Search,
    Settings,
    Playlists,
    PlaylistDetails,
    Favorites,
    TrackDetails,
    CreatePlaylist
}

@Composable
fun PlaylistHost(
    navController: NavHostController = rememberNavController(),
    searchViewModel: SearchViewModel
) {
    val playlistsViewModel: PlaylistsViewModel = viewModel(
        factory = PlaylistsViewModel.getViewModelFactory()
    )
    var selectedTrack by remember { mutableStateOf<AudioTrack?>(null) }

    NavHost(
        navController = navController,
        startDestination = PlaylistScreen.Main.name
    ) {
        composable(route = PlaylistScreen.Main.name) {
            MainScreen(
                onNavigateToSearch = { navController.navigate(PlaylistScreen.Search.name) },
                onNavigateToSettings = { navController.navigate(PlaylistScreen.Settings.name) },
                onNavigateToPlaylists = { navController.navigate(PlaylistScreen.Playlists.name) },
                onNavigateToFavorites = { navController.navigate(PlaylistScreen.Favorites.name) }
            )
        }

        composable(route = PlaylistScreen.Search.name) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = searchViewModel,
                onTrackClick = { track ->
                    selectedTrack = track
                    navController.navigate(PlaylistScreen.TrackDetails.name)
                }
            )
        }

        composable(route = PlaylistScreen.Settings.name) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = PlaylistScreen.Playlists.name) {
            PlaylistsScreen(
                playlistsViewModel = playlistsViewModel,
                onBackClick = { navController.popBackStack() },
                onNavigateToPlaylistDetails = { playlistId ->
                    navController.navigate("${PlaylistScreen.PlaylistDetails.name}/$playlistId")
                },
                onNavigateToCreatePlaylist = {
                    navController.navigate(PlaylistScreen.CreatePlaylist.name)
                }
            )
        }

        composable(route = PlaylistScreen.Favorites.name) {
            val favoritesViewModel: FavoritesViewModel = viewModel(
                factory = FavoritesViewModel.getViewModelFactory()
            )
            FavoritesScreen(
                viewModel = favoritesViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onTrackClick = { track ->
                    selectedTrack = track
                    navController.navigate(PlaylistScreen.TrackDetails.name)
                }
            )
        }

        composable(route = PlaylistScreen.TrackDetails.name) {
            selectedTrack?.let { track ->
                TrackDetailsScreen(
                    track = track,
                    playlistsViewModel = playlistsViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(route = PlaylistScreen.CreatePlaylist.name) {
            CreatePlaylistScreen(
                playlistsViewModel = playlistsViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = "${PlaylistScreen.PlaylistDetails.name}/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
            PlaylistScreen(
                index = playlistId.toInt(),
                navigateBack = { navController.popBackStack() },
                onTrackClick = { track ->
                    selectedTrack = track
                    navController.navigate(PlaylistScreen.TrackDetails.name)
                }
            )
        }
    }
}