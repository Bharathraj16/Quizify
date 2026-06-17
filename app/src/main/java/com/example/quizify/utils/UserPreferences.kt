package com.example.quizify.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    private val context: Context
) {
    private val SPLASH_SEEN = booleanPreferencesKey("splash_seen")

    val hasSeen: Flow<Boolean> = context.dataStore.data
        .map { it[SPLASH_SEEN] ?: false }

    suspend fun markSplashSeen() {
        context.dataStore.edit { it[SPLASH_SEEN] = true }
    }
}