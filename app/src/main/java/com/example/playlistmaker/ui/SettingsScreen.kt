package com.example.playlistmaker.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val whiteBackground = Color(0xFFFFFFFF)
    val blackTitle = Color(0xFF1A1B22)

    val shareUrl = stringResource(id = R.string.share_app_url)
    val supportEmail = stringResource(id = R.string.support_email)
    val supportSubject = stringResource(id = R.string.support_subject)
    val supportMessage = stringResource(id = R.string.support_message)
    val termsUrl = stringResource(id = R.string.terms_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = blackTitle)
            }
            Text(text = "Настройки", fontSize = 22.sp, fontWeight = FontWeight.Medium, color = blackTitle, modifier = Modifier.padding(start = 6.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        SettingsSwitchItem(title = "Тёмная тема")

        SettingsIconItem(
            title = stringResource(id = R.string.settings_share),
            icon = rememberVectorPainter(image = Icons.Default.Share),
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareUrl)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        )

        SettingsIconItem(
            title = stringResource(id = R.string.settings_support),
            icon = painterResource(id = R.drawable.support),
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$supportEmail")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
                    putExtra(Intent.EXTRA_SUBJECT, supportSubject)
                    putExtra(Intent.EXTRA_TEXT, supportMessage)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                }

                try {
                    context.startActivity(Intent.createChooser(emailIntent, "Написать в поддержку"))
                } catch (e: Exception) {
                    android.util.Log.e("Settings", "Ошибка при запуске почты", e)
                }
            }
        )

        SettingsIconItem(
            title = stringResource(id = R.string.settings_terms),
            icon = rememberVectorPainter(image = Icons.Default.KeyboardArrowRight),
            onClick = {
                val termsIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(termsIntent)
                } catch (e: ActivityNotFoundException) {}
            }
        )
    }
}

@Composable
fun SettingsSwitchItem(title: String) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().height(61.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 16.sp, color = Color.Black)
        Switch(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            colors = SwitchDefaults.colors(uncheckedTrackColor = Color(0xFFE6E8EB), uncheckedThumbColor = Color(0xFFAEAFB4))
        )
    }
}

@Composable
fun SettingsIconItem(title: String, icon: Painter, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(61.dp).clickable { onClick() }.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 16.sp, color = Color.Black)
        Icon(painter = icon, contentDescription = null, tint = Color(0xFFAEAFB4), modifier = Modifier.size(24.dp))
    }
}