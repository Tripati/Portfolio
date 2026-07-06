package com.tripaty.portfolio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripaty.portfolio.domain.model.PortfolioUiState
import com.tripaty.portfolio.domain.repository.PortfolioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val repository: PortfolioRepository,
) : ViewModel() {

    val uiState: StateFlow<PortfolioUiState> = repository.observePortfolio()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PortfolioUiState.Loading)

    private var initialized = false

    fun loadIfNeeded() {
        if (initialized) return
        initialized = true
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { repository.refresh(force = true) }
    }
}
