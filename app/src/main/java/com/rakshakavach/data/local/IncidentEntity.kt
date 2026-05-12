package com.rakshakavach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incident_log")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: String,
    val taskName: String,
    val description: String,
    val severity: String, // NEAR_MISS, MINOR, SERIOUS
    val timestamp: Long,
    val date: String
)
