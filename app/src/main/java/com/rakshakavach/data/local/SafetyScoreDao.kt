package com.rakshakavach.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SafetyScoreDao {
    @Query("SELECT * FROM safety_score WHERE id = 1")
    fun getSafetyScore(): Flow<SafetyScoreEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSafetyScore(score: SafetyScoreEntity)
}
