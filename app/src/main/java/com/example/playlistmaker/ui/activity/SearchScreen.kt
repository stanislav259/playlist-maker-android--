package com.example.playlistmaker.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.SearchState
import com.example.playlistmaker.SearchViewModel
import com.example.playlistmaker.data.network.Track
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    viewModel: SearchViewModel
) {
    val screenState by viewModel.searchScreenState.collectAsState()
    var searchText by remember { mutableStateOf("") }

    // Для отсрочки поиска (debounce)
    LaunchedEffect(searchText) {
        if (searchText.isNotEmpty()) {
            delay(2000) // Ждем 2 секунды после последнего ввода
            viewModel.search(searchText)
        } else {
            // Если текст очищен, сбрасываем состояние
            // Нужно добавить метод clearSearch() в ViewModel
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Поиск", color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Поиск",
                        tint = Color.Gray,
                        modifier = Modifier.clickable {
                            if (searchText.isNotEmpty()) {
                                viewModel.search(searchText)
                            }
                        }
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            // Сброс состояния поиска
                            // viewModel.clearSearch() // если добавите метод в ViewModel
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Очистить")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE6E8EB),
                    unfocusedContainerColor = Color(0xFFE6E8EB),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (screenState) {
                is SearchState.Initial -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Введите строку для поиска")
                    }
                }

                is SearchState.Searching -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is SearchState.Success -> {
                    val tracks = (screenState as SearchState.Success).list
                    if (tracks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ничего не найдено")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            itemsIndexed(tracks) { index, track ->
                                TrackListItem(track = track)
                                if (index < tracks.size - 1) {
                                    Divider(
                                        thickness = 0.5.dp,
                                        color = Color.LightGray,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                is SearchState.Fail -> {
                    val error = (screenState as SearchState.Fail).error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ошибка: $error", color = Color.Red)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackListItem(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = "Трек ${track.trackName}",
            modifier = Modifier
                .height(48.dp)
                .padding(end = 16.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = track.trackName,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black
            )
            Text(
                text = track.artistName,
                maxLines = 1,
                color = Color.Gray
            )
        }
        Text(
            text = track.trackTime,
            color = Color.Gray
        )
    }
}