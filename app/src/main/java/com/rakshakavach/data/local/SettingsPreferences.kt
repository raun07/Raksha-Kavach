package com.rakshakavach.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val LANGUAGE = stringPreferencesKey("language")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    val reminderHourFlow: Flow<Int> = dataStore.data.map { it[REMINDER_HOUR] ?: 7 }
    val reminderMinuteFlow: Flow<Int> = dataStore.data.map { it[REMINDER_MINUTE] ?: 0 }
    val languageFlow: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "English" }
    val isFirstLaunchFlow: Flow<Boolean> = dataStore.data.map { it[IS_FIRST_LAUNCH] ?: true }

    suspend fun saveReminderTime(hour: Int, minute: Int) {
        dataStore.edit { prefs ->
            prefs[REMINDER_HOUR] = hour
            prefs[REMINDER_MINUTE] = minute
        }
    }

    suspend fun saveLanguage(language: String) {
        dataStore.edit { prefs -> prefs[LANGUAGE] = language }
    }

    suspend fun setFirstLaunchCompleted() {
        dataStore.edit { prefs -> prefs[IS_FIRST_LAUNCH] = false }
    }
}
