package com.rakshakavach.ui

import androidx.lifecycle.ViewModel
import com.rakshakavach.domain.model.PPEItem
import com.rakshakavach.domain.model.RiskLevel
import com.rakshakavach.domain.model.TaskModel
import com.rakshakavach.util.RiskCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _selectedTask = MutableStateFlow<TaskModel?>(null)
    val selectedTask: StateFlow<TaskModel?> = _selectedTask.asStateFlow()

    private val _checkedItems = MutableStateFlow<Set<PPEItem>>(emptySet())
    val checkedItems: StateFlow<Set<PPEItem>> = _checkedItems.asStateFlow()

    private val _currentRiskLevel = MutableStateFlow(RiskLevel.LOW)
    val currentRiskLevel: StateFlow<RiskLevel> = _currentRiskLevel.asStateFlow()

    private val _currentRiskScore = MutableStateFlow(0)
    val currentRiskScore: StateFlow<Int> = _currentRiskScore.asStateFlow()

    private val _likelyInjuries = MutableStateFlow<List<String>>(emptyList())
    val likelyInjuries: StateFlow<List<String>> = _likelyInjuries.asStateFlow()

    private val _todayQuizCompleted = MutableStateFlow(false)
    val todayQuizCompleted: StateFlow<Boolean> = _todayQuizCompleted.asStateFlow()

    private val _todayQuizScore = MutableStateFlow(0)
    val todayQuizScore: StateFlow<Int> = _todayQuizScore.asStateFlow()

    private val _incidentReportedToday = MutableStateFlow(false)
    val incidentReportedToday: StateFlow<Boolean> = _incidentReportedToday.asStateFlow()

    fun selectTask(task: TaskModel) {
        _selectedTask.value = task
        _checkedItems.value = emptySet()
        recalculateRisk()
    }

    fun togglePPEItem(item: PPEItem) {
        val currentItems = _checkedItems.value.toMutableSet()
        if (currentItems.contains(item)) {
            currentItems.remove(item)
        } else {
            currentItems.add(item)
        }
        _checkedItems.value = currentItems
        recalculateRisk()
    }

    fun setQuizResult(score: Int) {
        _todayQuizScore.value = score
        _todayQuizCompleted.value = true
    }

    fun flagIncidentToday() {
        _incidentReportedToday.value = true
    }

    fun resetDay() {
        _selectedTask.value = null
        _checkedItems.value = emptySet()
        _currentRiskLevel.value = RiskLevel.LOW
        _currentRiskScore.value = 0
        _likelyInjuries.value = emptyList()
        _todayQuizCompleted.value = false
        _todayQuizScore.value = 0
    }

    private fun recalculateRisk() {
        val task = _selectedTask.value
        if (task == null) {
            _currentRiskLevel.value = RiskLevel.LOW
            _currentRiskScore.value = 0
            _likelyInjuries.value = emptyList()
            return
        }

        // Risk is calculated based on MANDATORY PPE items that are NOT checked
        val uncheckedPPEs = task.mandatoryPPE.filterNot { _checkedItems.value.contains(it) }
        
        val riskResult = RiskCalculator.calculateRisk(task, uncheckedPPEs)
        
        _currentRiskLevel.value = riskResult.level
        _likelyInjuries.value = riskResult.likelyInjuries
        
        var score = 0
        for (ppe in uncheckedPPEs) {
            score += task.riskWeight[ppe] ?: 0
        }
        _currentRiskScore.value = score
    }
}
