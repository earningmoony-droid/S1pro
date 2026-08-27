package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesDataStore(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val KEY_BENGALI = booleanPreferencesKey("is_bengali")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Default to dark mode (true) for modern creator studio aesthetic
            preferences[KEY_DARK_MODE] ?: true
        }

    val isBengali: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_BENGALI] ?: true
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
        }
    }

    suspend fun setBengali(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BENGALI] = enabled
        }
    }
}
