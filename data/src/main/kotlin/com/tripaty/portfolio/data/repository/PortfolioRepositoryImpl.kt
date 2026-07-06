package com.tripaty.portfolio.data.repository

import com.tripaty.portfolio.data.local.PortfolioLocalDataSource
import com.tripaty.portfolio.data.mapper.toDomain
import com.tripaty.portfolio.data.remote.PortfolioRemoteDataSource
import com.tripaty.portfolio.domain.model.Portfolio
import com.tripaty.portfolio.domain.model.PortfolioUiState
import com.tripaty.portfolio.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioRepositoryImpl @Inject constructor(
    private val remote: PortfolioRemoteDataSource,
    private val local: PortfolioLocalDataSource,
) : PortfolioRepository {

    private val mutex = Mutex()
    private val _state = MutableStateFlow<PortfolioUiState>(PortfolioUiState.Loading)
    private var cached: Portfolio? = null

    override fun observePortfolio(): Flow<PortfolioUiState> = _state.asStateFlow()

    override fun cachedPortfolio(): Portfolio? = cached

    override suspend fun refresh(force: Boolean): Portfolio? = mutex.withLock {
        if (!force && cached != null) {
            _state.value = PortfolioUiState.Success(cached!!, fromCache = true)
        }

        val localDto = local.read()
        if (localDto != null) {
            cached = localDto.toDomain()
            _state.value = PortfolioUiState.Success(cached!!, fromCache = true)
        }

        return try {
            val remoteDto = remote.fetch()
            local.write(remoteDto)
            cached = remoteDto.toDomain()
            _state.value = PortfolioUiState.Success(cached!!, fromCache = false)
            cached
        } catch (e: Exception) {
            if (cached != null) {
                _state.value = PortfolioUiState.Error(e.message ?: "Network error", cached)
            } else {
                _state.value = PortfolioUiState.Error(e.message ?: "Network error", null)
            }
            cached
        }
    }
}
