package com.example.playlistmaker.ui.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.PlaylistsViewModel
import com.example.playlistmaker.Playlist
import com.example.playlistmaker.data.network.Track
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    trackName: String,
    artistName: String,
    onBackClick: () -> Unit,
    viewModel: PlaylistsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var trackState by remember { mutableStateOf<Track?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())

    LaunchedEffect(trackName, artistName) {
        val searchTemplate = Track(trackName = trackName, artistName = artistName, trackTime = "0:00")
        val foundTrack = viewModel.isExist(searchTemplate)
        trackState = foundTrack ?: searchTemplate
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О треке", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        val track = trackState
        if (track != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_music),
                        contentDescription = "Обложка трека",
                        modifier = Modifier.size(160.dp),
                        colorFilter = ColorFilter.tint(Color.LightGray)
                    )

                    Text(
                        text = track.trackName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Text(
                        text = track.artistName,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                val nextFavoriteState = !track.favorite
                                viewModel.toggleFavorite(track, nextFavoriteState)
                                trackState = track.copy(favorite = nextFavoriteState)
                            }
                        }) {
                            Icon(
                                imageVector = if (track.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "В избранное",
                                tint = if (track.favorite) Color.Red else Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        IconButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = "Добавить в плейлист",
                                tint = Color.Gray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                if (showBottomSheet) {
                    val sheetState = rememberModalBottomSheetState()
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState,
                        containerColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Добавить в плейлист",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp),
                                color = Color.Black
                            )

                            if (playlists.isEmpty()) {
                                Text(
                                    text = "Плейлисты отсутствуют.\nСоздайте их во вкладке Плейлисты.",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                    items(playlists) { playlist ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    coroutineScope.launch {
                                                        viewModel.insertTrackToPlaylist(track, playlist.id)
                                                        trackState = track.copy(playlistId = playlist.id)
                                                        showBottomSheet = false
                                                    }
                                                }
                                                .padding(vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = playlist.name,
                                                fontSize = 16.sp,
                                                color = Color.Black
                                            )
                                        }
                                        HorizontalDivider(thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}