package com.example.playlistmaker.ui.activity

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        fontWeight = FontWeight.Medium,
                        color = ThemeManager.AppTextColor
                    )
                },
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
                    titleContentColor = ThemeManager.AppTextColor,
                    navigationIconContentColor = ThemeManager.AppTextColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ThemeManager.AppBackgroundColor)
                .padding(paddingValues)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsItem(
                text = stringResource(id = R.string.settings_dark_theme),
                action = { SettingsSwitch() }
            )
            Spacer(modifier = Modifier.height(35.dp))
            SettingsItem(
                text = stringResource(id = R.string.settings_share_app),
                action = { SettingsIcon(Icons.Filled.Share) },
                onClick = { shareApp(context) }
            )
            Spacer(modifier = Modifier.height(35.dp))
            SettingsItem(
                text = stringResource(id = R.string.settings_write_to_support),
                action = { SettingsIcon(Icons.Outlined.SupportAgent) },
                onClick = { writeToSupport(context) }
            )
            Spacer(modifier = Modifier.height(35.dp))
            SettingsItem(
                text = stringResource(id = R.string.settings_user_agreement),
                action = { SettingsIcon(Icons.AutoMirrored.Filled.ArrowForwardIos) },
                onClick = { openUserAgreement(context) }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    text: String,
    onClick: (() -> Unit)? = null,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f),
            color = ThemeManager.AppTextColor
        )
        action()
    }
}

@Composable
private fun SettingsSwitch() {
    Switch(
        checked = ThemeManager.isDarkTheme,
        onCheckedChange = { isChecked ->
            ThemeManager.switchTheme(isChecked)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color(0xFF3772E7),
            uncheckedTrackColor = Color.Gray,
            uncheckedThumbColor = Color(0xFFE6E8EB)
        )
    )
}

@Composable
private fun SettingsIcon(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.Gray
    )
}

private fun shareApp(context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_link))
    }
    context.startActivity(
        Intent.createChooser(intent, context.getString(R.string.share))
    )
}

private fun writeToSupport(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.developer_email)))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_email_subject))
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_email_body))
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        // Обработка случая, если почтовый клиент не найден
    }
}

private fun openUserAgreement(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, context.getString(R.string.offer_url).toUri())
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        // Обработка случая, если браузер не найден
    }
}