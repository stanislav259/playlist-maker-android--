package com.example.playlistmaker.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.PlaylistsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
    onBackClick: () -> Unit,
    viewModel: PlaylistsViewModel
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Флаг для блокировки кнопки, если поле "Название" пустое
    val isButtonEnabled = name.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Новый плейлист",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()), // Позволяет прокручивать экран при открытии клавиатуры
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Плейсхолдер для обложки плейлиста (большой серый квадрат со скругленными углами)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Строго квадратная форма
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE6E8EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_music), // Используйте ваш ресурс для добавления фото
                    contentDescription = "Добавить обложку",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFFAEAFB4)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Поле ввода "Название*"
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3772E7),     // Синяя рамка при фокусе
                    unfocusedBorderColor = Color(0xFFAEAFB4),   // Серая рамка без фокуса
                    focusedLabelColor = Color(0xFF3772E7),       // Синий лейбл при фокусе
                    unfocusedLabelColor = Color(0xFFAEAFB4),     // Серый лейбл без фокуса
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле ввода "Описание"
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3772E7),
                    unfocusedBorderColor = Color(0xFFAEAFB4),
                    focusedLabelColor = Color(0xFF3772E7),
                    unfocusedLabelColor = Color(0xFFAEAFB4),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            // Динамический отступ, выталкивающий кнопку вниз экрана
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка "Создать"
            Button(
                onClick = {
                    if (isButtonEnabled) {
                        viewModel.createNewPlayList(name, description)
                        onBackClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 8.dp),
                enabled = isButtonEnabled,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3772E7),         // Синий цвет активной кнопки
                    disabledContainerColor = Color(0xFFB3B4B9),  // Серый цвет неактивной кнопки
                    contentColor = Color.White,                 // Белый цвет текста активной кнопки
                    disabledContentColor = Color.White          // Белый цвет текста неактивной кнопки
                )
            ) {
                Text(
                    text = "Создать",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}