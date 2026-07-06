package com.tripaty.portfolio.domain.repository

import com.tripaty.portfolio.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    val themeMode: Flow<ThemeMode>
    suspend fun setThemeMode(mode: ThemeMode)
    val emailRevealed: Flow<Boolean>
    suspend fun setEmailRevealed(revealed: Boolean)
}
