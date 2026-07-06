package com.tripaty.portfolio.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tripaty.portfolio.domain.model.ThemeMode
import com.tripaty.portfolio.domain.repository.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore("portfolio_prefs")

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PreferencesRepository {

    private val themeKey = stringPreferencesKey("theme_mode")
    private val emailKey = booleanPreferencesKey("email_revealed")

    override val themeMode: Flow<ThemeMode> = context.prefsDataStore.data.map { prefs ->
        runCatching { ThemeMode.valueOf(prefs[themeKey] ?: ThemeMode.DARK.name) }
            .getOrDefault(ThemeMode.DARK)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.prefsDataStore.edit { it[themeKey] = mode.name }
    }

    override val emailRevealed: Flow<Boolean> = context.prefsDataStore.data.map { it[emailKey] ?: false }

    override suspend fun setEmailRevealed(revealed: Boolean) {
        context.prefsDataStore.edit { it[emailKey] = revealed }
    }
}
