package com.example.playlistmaker.ui.activity

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.Playlist
import com.example.playlistmaker.PlaylistsViewModel
import com.example.playlistmaker.data.ThemeManager  

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 10.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (playlist.coverImageUri != null) {
            AsyncImage(
                model = android.net.Uri.parse(playlist.coverImageUri),
                contentDescription = playlist.name,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ThemeManager.AppCardColor),  
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ThemeManager.AppCardColor),  
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = playlist.name,
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(Color(0xFFAEAFB4))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = playlist.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = ThemeManager.AppTextColor  
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = getTrackCountString(playlist.tracks.size),
                fontSize = 13.sp,
                color = Color(0xFFAEAFB4)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    playlistsViewModel: PlaylistsViewModel,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    navigateBack: () -> Unit
) {
    val playlists by playlistsViewModel.playlists.collectAsState(emptyList())
    val context = LocalContext.current

    var selectedSourcePlaylist by remember { mutableStateOf<Playlist?>(null) }
    var targetPlaylistToMerge by remember { mutableStateOf<Playlist?>(null) }
    var showMergeBottomSheet by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Плейлисты",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp).size(56.dp),
                onClick = { addNewPlaylist() },
                containerColor = Color(0xFFB3B4B9),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Добавить", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(ThemeManager.AppBackgroundColor)  
                .padding(paddingValues)
        ) {
            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("У вас пока нет плейлистов", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(playlists.size) { index ->
                        PlaylistListItem(
                            playlist = playlists[index],
                            onClick = { navigateToPlaylist(playlists[index].id) },
                            onLongClick = {
                                selectedSourcePlaylist = playlists[index]
                                showMergeBottomSheet = true
                            }
                        )
                    }
                }
            }

            if (showMergeBottomSheet && selectedSourcePlaylist != null) {
                val sheetState = rememberModalBottomSheetState()
                val otherPlaylists = playlists.filter { it.id != selectedSourcePlaylist?.id }

                ModalBottomSheet(
                    onDismissRequest = { showMergeBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = ThemeManager.AppBackgroundColor  
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(
                            text = "Объединить с плейлистом",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = ThemeManager.AppTextColor  
                        )

                        if (otherPlaylists.isEmpty()) {
                            Text("Нет других плейлистов.", color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(otherPlaylists.size) { idx ->
                                    val targetPlaylist = otherPlaylists[idx]
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                targetPlaylistToMerge = targetPlaylist
                                                showConfirmationDialog = true
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(targetPlaylist.name, fontSize = 16.sp, color = ThemeManager.AppTextColor)  
                                    }
                                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }

            if (showConfirmationDialog && selectedSourcePlaylist != null && targetPlaylistToMerge != null) {
                AlertDialog(
                    onDismissRequest = { showConfirmationDialog = false },
                    title = { Text("Объединение плейлистов", fontWeight = FontWeight.Bold) },
                    text = { Text("Объединить \"${selectedSourcePlaylist?.name}\" с \"${targetPlaylistToMerge?.name}\"?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmationDialog = false
                                showMergeBottomSheet = false
                                val source = selectedSourcePlaylist
                                val target = targetPlaylistToMerge
                                if (source != null && target != null) {
                                    playlistsViewModel.mergePlaylists(source, target) {
                                        Toast.makeText(context, "${source.name} слит с ${target.name}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        ) {
                            Text("Объединить", color = Color(0xFF3772E7), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmationDialog = false }) {
                            Text("Отмена", color = Color.Gray)
                        }
                    },
                    containerColor = ThemeManager.AppBackgroundColor,  
                    titleContentColor = ThemeManager.AppTextColor,  
                    textContentColor = ThemeManager.AppTextColor  
                )
            }
        }
    }
}