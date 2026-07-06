package com.tripaty.portfolio.domain.repository

import com.tripaty.portfolio.domain.model.Portfolio
import com.tripaty.portfolio.domain.model.PortfolioUiState
import kotlinx.coroutines.flow.Flow

interface PortfolioRepository {
    fun observePortfolio(): Flow<PortfolioUiState>
    suspend fun refresh(force: Boolean = false): Portfolio?
    fun cachedPortfolio(): Portfolio?
}
