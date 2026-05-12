package com.rakshakavach.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        IncidentEntity::class,
        SafetyScoreEntity::class,
        ChecklistHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RakshaKavachDatabase : RoomDatabase() {
    abstract val incidentDao: IncidentDao
    abstract val safetyScoreDao: SafetyScoreDao
    abstract val checklistHistoryDao: ChecklistHistoryDao
}
