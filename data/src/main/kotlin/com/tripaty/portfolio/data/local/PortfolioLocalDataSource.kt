package com.tripaty.portfolio.data.local

import android.content.Context
import com.tripaty.portfolio.data.remote.PortfolioDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortfolioLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
    private val json: Json,
) {
    private val cacheFile = File(context.filesDir, "portfolio_cache.json")

    suspend fun read(): PortfolioDto? = withContext(Dispatchers.IO) {
        if (!cacheFile.exists()) return@withContext null
        runCatching {
            json.decodeFromString(PortfolioDto.serializer(), cacheFile.readText())
        }.getOrNull()
    }

    suspend fun write(dto: PortfolioDto) = withContext(Dispatchers.IO) {
        cacheFile.writeText(json.encodeToString(dto))
    }
}
