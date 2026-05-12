package com.rakshakavach.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakshakavach.data.local.IncidentEntity
import com.rakshakavach.data.local.SafetyScoreEntity
import com.rakshakavach.data.repository.GeminiRepository
import com.rakshakavach.domain.repository.IncidentRepository
import com.rakshakavach.domain.repository.SafetyScoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SafetyScoreViewModel @Inject constructor(
    private val safetyScoreRepository: SafetyScoreRepository,
    private val incidentRepository: IncidentRepository,
    private val geminiRepository: GeminiRepository
) : ViewModel() {

    val safetyScore: StateFlow<SafetyScoreEntity?> = safetyScoreRepository.getSafetyScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentIncidents: StateFlow<List<IncidentEntity>> = incidentRepository.getAllIncidents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dailyTip = MutableStateFlow("Always wear your mandatory PPE and stay alert.")
    val dailyTip: StateFlow<String> = _dailyTip.asStateFlow()

    init {
        fetchDailyTip()
        // Initialize an empty score if none exists
        viewModelScope.launch {
            safetyScoreRepository.getSafetyScore().collect { score ->
                if (score == null) {
                    safetyScoreRepository.updateSafetyScore(
                        SafetyScoreEntity(
                            currentScore = 0,
                            currentStreak = 0,
                            bestStreak = 0,
                            lastCheckInDate = "",
                            totalDaysCheckedIn = 0,
                            badges = ""
                        )
                    )
                }
            }
        }
    }

    private fun fetchDailyTip() {
        viewModelScope.launch {
            try {
                // Defaulting to "General Site Work" for the daily tip on Home screen
                val tip = geminiRepository.getSafetyTip("General Site Work")
                _dailyTip.value = tip
            } catch (e: Exception) {
                // Fallback handled in repository, but just in case
            }
        }
    }
}
