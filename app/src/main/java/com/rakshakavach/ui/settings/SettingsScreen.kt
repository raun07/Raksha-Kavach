package com.rakshakavach.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rakshakavach.util.NotificationScheduler
import com.rakshakavach.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val reminderHour by viewModel.reminderHour.collectAsState()
    val reminderMinute by viewModel.reminderMinute.collectAsState()
    val language by viewModel.language.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("NOTIFICATIONS", color = MidGrey, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Daily reminder time", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = String.format("%02d:%02d", reminderHour, reminderMinute),
                            color = YellowPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        TimePickerDialog(
                            context,
                            { _, selectedHour, selectedMinute ->
                                viewModel.saveReminderTime(selectedHour, selectedMinute)
                                NotificationScheduler.scheduleDaily(context, selectedHour, selectedMinute)
                            },
                            reminderHour, reminderMinute, true
                        ).show()
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Time", tint = YellowPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("LANGUAGE", color = MidGrey, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    LanguageTab(
                        label = "English",
                        isSelected = language == "English",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.saveLanguage("English") }
                    )
                    LanguageTab(
                        label = "Hindi",
                        isSelected = language == "Hindi",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.saveLanguage("Hindi") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("DATA MANAGEMENT", color = MidGrey, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                TextButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Reset Safety Score", color = RiskCritical, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Raksha-Kavach v1.0",
                color = MidGrey,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Safety Score?", color = Color.White) },
                text = { Text("This action cannot be undone. You will lose your score, streaks, and all earned badges.", color = MidGrey) },
                containerColor = DarkSurface,
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetSafetyScore()
                        showResetDialog = false
                    }) {
                        Text("Reset", color = RiskCritical, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}

@Composable
fun LanguageTab(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(if (isSelected) YellowPrimary.copy(alpha = 0.2f) else Color.Transparent)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) YellowPrimary else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
