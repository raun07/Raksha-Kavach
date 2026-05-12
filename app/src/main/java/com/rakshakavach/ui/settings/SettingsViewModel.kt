package com.rakshakavach.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakshakavach.data.local.SafetyScoreEntity
import com.rakshakavach.data.local.SettingsPreferences
import com.rakshakavach.domain.repository.SafetyScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences,
    private val safetyScoreRepository: SafetyScoreRepository
) : ViewModel() {

    val reminderHour = settingsPreferences.reminderHourFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 7)
    val reminderMinute = settingsPreferences.reminderMinuteFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val language = settingsPreferences.languageFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    fun saveReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsPreferences.saveReminderTime(hour, minute)
        }
    }

    fun saveLanguage(lang: String) {
        viewModelScope.launch {
            settingsPreferences.saveLanguage(lang)
        }
    }

    fun resetSafetyScore() {
        viewModelScope.launch {
            val resetScore = SafetyScoreEntity(
                id = 1,
                currentScore = 0,
                currentStreak = 0,
                bestStreak = 0,
                lastCheckInDate = "",
                totalDaysCheckedIn = 0,
                badges = ""
            )
            safetyScoreRepository.updateSafetyScore(resetScore)
        }
    }
}
