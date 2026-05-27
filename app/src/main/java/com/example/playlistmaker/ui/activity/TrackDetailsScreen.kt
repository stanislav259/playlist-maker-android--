package com.example.playlistmaker.ui.activity

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.PlaylistsViewModel
import com.example.playlistmaker.Playlist
import com.example.playlistmaker.data.ThemeManager
import com.example.playlistmaker.data.network.Track
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    trackName: String,
    artistName: String,
    trackTime: String,
    artworkUrl: String,
    onBackClick: () -> Unit,
    viewModel: PlaylistsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var trackState by remember { mutableStateOf<Track?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val playlists by viewModel.playlists.collectAsState(initial = emptyList())

    LaunchedEffect(trackName, artistName) {
        val calculatedId = (trackName + artistName).hashCode().toLong()
        val searchTemplate = Track(
            id = calculatedId,
            trackName = trackName,
            artistName = artistName,
            trackTime = trackTime,
            artworkUrl100 = artworkUrl
        )
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
                            tint = ThemeManager.AppTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ThemeManager.AppBackgroundColor,
                    titleContentColor = ThemeManager.AppTextColor
                )
            )
        }
    ) { padding ->
        val track = trackState
        if (track != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ThemeManager.AppBackgroundColor)
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    AsyncImage(
                        model = track.artworkUrl100,
                        placeholder = painterResource(id = R.drawable.ic_music),
                        error = painterResource(id = R.drawable.ic_music),
                        contentDescription = "Обложка",
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ThemeManager.AppCardColor),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = track.trackName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Start,
                        color = ThemeManager.AppTextColor,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = track.artistName,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(28.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(51.dp)
                                .clip(CircleShape)
                                .background(ThemeManager.AppCardColor)
                                .clickable { showBottomSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Добавить в плейлист",
                                tint = ThemeManager.AppTextColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(51.dp)
                                .clip(CircleShape)
                                .background(ThemeManager.AppCardColor)
                                .clickable {
                                    coroutineScope.launch {
                                        val nextFavoriteState = !track.favorite
                                        viewModel.toggleFavorite(track, nextFavoriteState)
                                        trackState = track.copy(favorite = nextFavoriteState)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (track.favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "В избранное",
                                tint = if (track.favorite) Color.Red else ThemeManager.AppTextColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Длительность",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = track.trackTime,
                            fontSize = 14.sp,
                            color = ThemeManager.AppTextColor,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                if (showBottomSheet) {
                    val sheetState = rememberModalBottomSheetState()
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState,
                        containerColor = ThemeManager.AppBackgroundColor
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
                                color = ThemeManager.AppTextColor
                            )

                            if (playlists.isEmpty()) {
                                Text(
                                    text = "Плейлисты отсутствуют.",
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
                                                color = ThemeManager.AppTextColor
                                            )
                                        }
                                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
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