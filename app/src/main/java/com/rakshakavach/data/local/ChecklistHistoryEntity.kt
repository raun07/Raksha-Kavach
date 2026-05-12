package com.rakshakavach.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_history")
data class ChecklistHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val taskId: String,
    val completedItems: String, // Comma separated string or JSON string
    val wasFullyCompleted: Boolean
)
