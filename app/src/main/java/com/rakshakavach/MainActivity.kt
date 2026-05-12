package com.rakshakavach

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.rakshakavach.data.local.SettingsPreferences
import com.rakshakavach.ui.navigation.RakshaKavachNavGraph
import com.rakshakavach.ui.theme.RakshaKavachTheme
import com.rakshakavach.util.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleNotificationIfFirstLaunch()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkNotificationPermissionAndSchedule()

        setContent {
            RakshaKavachTheme {
                RakshaKavachNavGraph()
            }
        }
    }

    private fun checkNotificationPermissionAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                scheduleNotificationIfFirstLaunch()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            scheduleNotificationIfFirstLaunch()
        }
    }

    private fun scheduleNotificationIfFirstLaunch() {
        lifecycleScope.launch {
            val isFirstLaunch = settingsPreferences.isFirstLaunchFlow.first()
            if (isFirstLaunch) {
                NotificationScheduler.scheduleDaily(this@MainActivity, 7, 0)
                settingsPreferences.setFirstLaunchCompleted()
            }
        }
    }
}