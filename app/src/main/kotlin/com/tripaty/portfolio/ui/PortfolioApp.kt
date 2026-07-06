package com.tripaty.portfolio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.tripaty.portfolio.domain.model.PortfolioUiState
import com.tripaty.portfolio.tiru.TiruViewModel
import com.tripaty.portfolio.ui.components.PortfolioLoadingShimmer
import com.tripaty.portfolio.ui.screens.AboutScreen
import com.tripaty.portfolio.ui.screens.MoreScreen
import com.tripaty.portfolio.ui.screens.SkillsScreen
import com.tripaty.portfolio.ui.screens.TiruSheet
import com.tripaty.portfolio.ui.screens.WorkScreen
import com.tripaty.portfolio.ui.theme.AccentGold
import com.tripaty.portfolio.ui.theme.ThemeViewModel
import com.tripaty.portfolio.util.openCustomTab
import kotlinx.coroutines.launch

private enum class Tab(val label: String) {
    ABOUT("About"), SKILLS("Skills"), WORK("Work"), MORE("More")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioApp(
    portfolioViewModel: PortfolioViewModel = hiltViewModel(),
    tiruViewModel: TiruViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
) {
    val uiState by portfolioViewModel.uiState.collectAsState()
    val tiruState by tiruViewModel.uiState.collectAsState()
    val themeMode by themeViewModel.themeMode.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var tiruOpen by remember { mutableStateOf(false) }
    val emailRevealed by themeViewModel.emailRevealed.collectAsState(initial = false)
    var refreshing by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { portfolioViewModel.loadIfNeeded() }

    LaunchedEffect(uiState) {
        if (uiState !is PortfolioUiState.Loading) refreshing = false
        if (uiState is PortfolioUiState.Error) {
            snackbar.showSnackbar((uiState as PortfolioUiState.Error).message)
        }
    }

    val portfolio = when (val state = uiState) {
        is PortfolioUiState.Success -> state.portfolio
        is PortfolioUiState.Error -> state.cached
        PortfolioUiState.Loading -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tripaty Kumar Sahu") })
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                when (tab) {
                                    Tab.ABOUT -> Icons.Default.Person
                                    Tab.SKILLS -> Icons.AutoMirrored.Filled.List
                                    Tab.WORK -> Icons.Default.Work
                                    Tab.MORE -> Icons.Default.MoreHoriz
                                },
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    tiruViewModel.onFabClick()
                    tiruOpen = true
                },
                containerColor = AccentGold,
            ) {
                Icon(Icons.Default.Chat, contentDescription = "Open Tiru")
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                refreshing = true
                portfolioViewModel.refresh()
            },
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            when {
                portfolio == null && uiState is PortfolioUiState.Loading -> PortfolioLoadingShimmer()
                portfolio != null -> {
                    val p = portfolio!!
                    val contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    val onLinkedIn = { openCustomTab(context, p.profile.linkedin) }
                    when (Tab.entries[selectedTab]) {
                        Tab.ABOUT -> AboutScreen(p, emailRevealed, themeViewModel::revealEmail, onLinkedIn, contentPadding)
                        Tab.SKILLS -> SkillsScreen(p, contentPadding)
                        Tab.WORK -> WorkScreen(p, { openCustomTab(context, it) }, contentPadding)
                        Tab.MORE -> MoreScreen(
                            portfolio = p,
                            emailRevealed = emailRevealed,
                            themeMode = themeMode,
                            onEmailReveal = themeViewModel::revealEmail,
                            onLinkedIn = onLinkedIn,
                            onOpenCert = { openCustomTab(context, it) },
                            onShareResume = {
                                scope.launch {
                                    // ResumeHelper injected via entry point would be ideal; use direct download in VM later
                                    openCustomTab(context, "https://tripati.github.io/Portfolio/resume.pdf")
                                }
                            },
                            onThemeChange = themeViewModel::setThemeMode,
                            contentPadding = contentPadding,
                        )
                    }
                }
                else -> Box(Modifier.fillMaxSize()) {
                    Text("Unable to load portfolio. Pull down to retry.", Modifier.padding(16.dp))
                }
            }
        }
    }

    TiruSheet(
        visible = tiruOpen,
        uiState = tiruState,
        portfolio = portfolio,
        onDismiss = { tiruOpen = false },
        onSend = tiruViewModel::sendMessage,
        onClear = tiruViewModel::clearChat,
        onShown = tiruViewModel::showWelcomeIfEmpty,
    )
}
