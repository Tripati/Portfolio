package com.tripaty.portfolio.domain.model

sealed interface PortfolioUiState {
    data object Loading : PortfolioUiState
    data class Success(val portfolio: Portfolio, val fromCache: Boolean = false) : PortfolioUiState
    data class Error(val message: String, val cached: Portfolio? = null) : PortfolioUiState
}
