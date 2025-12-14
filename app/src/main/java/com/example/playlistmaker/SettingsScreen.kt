package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings), fontWeight = FontWeight.Medium) },
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
                .padding(top = 24.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsItem(
                text = stringResource(id = R.string.settings_dark_theme),
                action = { SettingsSwitch() }
            )
            Spacer(modifier = Modifier.height(20.dp))
            SettingsItem(
                text = stringResource(id = R.string.settings_share_app),
                action = { SettingsIcon(Icons.Filled.Share) },
                onClick = { shareApp(context) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            SettingsItem(
                text = stringResource(id = R.string.settings_write_to_support),
                action = { SettingsIcon(Icons.Outlined.SupportAgent) },
                onClick = { writeToSupport(context) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            SettingsItem(
                text = stringResource(id = R.string.settings_user_agreement),
                action = { SettingsIcon(Icons.Default.ArrowForwardIos) },
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
            modifier = Modifier.weight(1f)
        )
        action()
    }
}

@Composable
private fun SettingsSwitch() {
    val isChecked = remember { mutableStateOf(false) }
    Switch(
        checked = isChecked.value,
        onCheckedChange = { isChecked.value = it },
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
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.developer_email)))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_email_subject))
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_email_body))
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Обработка случая, если почтовый клиент не найден
    }
}

private fun openUserAgreement(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.offer_url)))
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Обработка случая, если браузер не найден
    }
}