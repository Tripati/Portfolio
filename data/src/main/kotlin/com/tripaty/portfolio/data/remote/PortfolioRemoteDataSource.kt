package com.tripaty.portfolio.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Named

class PortfolioRemoteDataSource @Inject constructor(
    private val client: OkHttpClient,
    @Named("portfolioBaseUrl") private val baseUrl: String,
    private val json: Json,
) {
    suspend fun fetch(): PortfolioDto = withContext(Dispatchers.IO) {
        val url = baseUrl.trimEnd('/') + "/portfolio.json"
        val request = Request.Builder().url(url).get().build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code}")
            }
            val body = response.body?.string() ?: throw IllegalStateException("Empty response")
            json.decodeFromString(PortfolioDto.serializer(), body)
        }
    }
}
