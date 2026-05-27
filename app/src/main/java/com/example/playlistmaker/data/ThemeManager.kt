package com.example.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_DARK_THEME = "key_dark_theme"
    private lateinit var sharedPreferences: SharedPreferences

    private val _isDarkTheme = mutableStateOf(false)
    val isDarkTheme: Boolean get() = _isDarkTheme.value

    val AppBackgroundColor: Color
        @Composable get() = if (isDarkTheme) Color(0xFF121212) else Color.White

    val AppTextColor: Color
        @Composable get() = if (isDarkTheme) Color.White else Color.Black

    val AppCardColor: Color
        @Composable get() = if (isDarkTheme) Color(0xFF2C2C2C) else Color(0xFFE6E8EB)

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkTheme.value = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    fun switchTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}