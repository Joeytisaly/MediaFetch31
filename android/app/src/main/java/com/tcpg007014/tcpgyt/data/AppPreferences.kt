package com.tcpg007014.tcpgyt.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tcpg007014.tcpgyt.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tcpgyt_prefs")

object AppPreferences {
    private val THEME_KEY = stringPreferencesKey("theme")

    fun themeFlow(context: Context): Flow<AppTheme> = context.dataStore.data.map { prefs ->
        val name = prefs[THEME_KEY] ?: AppTheme.Blush.name
        AppTheme.entries.find { it.name == name } ?: AppTheme.Blush
    }

    suspend fun saveTheme(context: Context, theme: AppTheme) {
        context.dataStore.edit { prefs -> prefs[THEME_KEY] = theme.name }
    }
}
