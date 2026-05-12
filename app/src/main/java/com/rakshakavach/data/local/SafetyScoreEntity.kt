package com.rakshakavach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safety_score")
data class SafetyScoreEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton row
    val currentScore: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val lastCheckInDate: String,
    val totalDaysCheckedIn: Int,
    val badges: String // comma-separated
)
