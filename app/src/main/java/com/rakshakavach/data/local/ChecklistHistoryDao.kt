package com.rakshakavach.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistHistoryDao {
    @Query("SELECT * FROM checklist_history ORDER BY date DESC")
    fun getAllChecklistHistory(): Flow<List<ChecklistHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistHistory(history: ChecklistHistoryEntity)
}
