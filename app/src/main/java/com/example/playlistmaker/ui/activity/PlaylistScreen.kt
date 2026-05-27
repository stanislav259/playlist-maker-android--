package com.example.playlistmaker.ui.activity

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.PlaylistViewModel
import com.example.playlistmaker.data.ThemeManager
import com.example.playlistmaker.data.network.Track

private fun getTrackCountString(count: Int): String {
    val lastDigit = count % 10
    val lastTwoDigits = count % 100
    return when {
        lastTwoDigits in 11..19 -> "$count треков"
        lastDigit == 1 -> "$count трек"
        lastDigit in 2..4 -> "$count трека"
        else -> "$count треков"
    }
}

private fun calculateTotalMinutes(tracks: List<Track>): Int {
    val totalSeconds = tracks.sumOf { track ->
        val parts = track.trackTime.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            minutes * 60 + seconds
        } else 0
    }
    return totalSeconds / 60
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaylistViewModel,
    navigateToTrack: (Track) -> Unit,
    navigateBack: () -> Unit
) {
    val playlistState by viewModel.playlist.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Плейлист", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
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
        val playlist = playlistState
        if (playlist != null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(ThemeManager.AppBackgroundColor)
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ThemeManager.AppCardColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (playlist.coverImageUri != null) {
                        AsyncImage(
                            model = Uri.parse(playlist.coverImageUri),
                            contentDescription = "Обложка плейлиста",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_music),
                            contentDescription = "Обложка плейлиста",
                            modifier = Modifier.size(80.dp),
                            colorFilter = ColorFilter.tint(Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = playlist.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeManager.AppTextColor
                )

                Spacer(modifier = Modifier.height(8.dp))
                val totalMinutes = calculateTotalMinutes(playlist.tracks)
                val tracksCountString = getTrackCountString(playlist.tracks.size)
                Text(
                    text = "$totalMinutes минут • $tracksCountString",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                if (playlist.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = playlist.description,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Треки",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeManager.AppTextColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (playlist.tracks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "В этом плейлисте пока нет треков",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                        items(playlist.tracks) { track ->
                            TrackListItem(
                                track = track,
                                onClick = { navigateToTrack(track) }
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}