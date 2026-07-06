package com.tripaty.portfolio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.tripaty.portfolio.ui.PortfolioApp
import com.tripaty.portfolio.ui.theme.PortfolioTheme
import com.tripaty.portfolio.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val themeMode by themeViewModel.themeMode.collectAsState()
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                com.tripaty.portfolio.domain.model.ThemeMode.DARK -> true
                com.tripaty.portfolio.domain.model.ThemeMode.LIGHT -> false
                com.tripaty.portfolio.domain.model.ThemeMode.SYSTEM -> systemDark
            }
            PortfolioTheme(darkTheme = darkTheme) {
                PortfolioApp()
            }
        }
    }
}
