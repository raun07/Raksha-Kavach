package com.rakshakavach.domain.repository

import com.rakshakavach.data.local.SafetyScoreDao
import com.rakshakavach.data.local.SafetyScoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SafetyScoreRepository @Inject constructor(
    private val safetyScoreDao: SafetyScoreDao
) {
    fun getSafetyScore(): Flow<SafetyScoreEntity?> = safetyScoreDao.getSafetyScore()
    
    suspend fun updateSafetyScore(score: SafetyScoreEntity) {
        safetyScoreDao.updateSafetyScore(score)
    }
    
    suspend fun completeDay(quizScore: Int, checklistCompleted: Boolean, incidentReported: Boolean) {
        val current = safetyScoreDao.getSafetyScore().firstOrNull() ?: SafetyScoreEntity(
            currentScore = 0,
            currentStreak = 0,
            bestStreak = 0,
            lastCheckInDate = "",
            totalDaysCheckedIn = 0,
            badges = ""
        )
        
        var newStreak = current.currentStreak
        var newScore = current.currentScore
        val newBadges = current.badges.split(",").filter { it.isNotBlank() }.toMutableSet()
        val newTotalDays = current.totalDaysCheckedIn + 1
        
        if (incidentReported) {
            newStreak = 0
            newScore = maxOf(0, newScore - 5)
        } else if (checklistCompleted) {
            newStreak++
            newScore += (10 + quizScore * 2)
            
            if (newStreak >= 7) newBadges.add("Safe Starter")
            if (newStreak >= 30) newBadges.add("Safety Champion")
            if (newStreak >= 100) newBadges.add("Safety Legend")
        }
        
        val newBestStreak = maxOf(current.bestStreak, newStreak)
        
        val updated = current.copy(
            currentScore = newScore,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            totalDaysCheckedIn = newTotalDays,
            badges = newBadges.joinToString(",")
        )
        
        safetyScoreDao.updateSafetyScore(updated)
    }
}
