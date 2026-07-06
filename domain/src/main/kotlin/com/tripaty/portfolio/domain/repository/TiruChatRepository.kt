package com.tripaty.portfolio.domain.repository

import com.tripaty.portfolio.domain.model.TiruMessage
import kotlinx.coroutines.flow.Flow

interface TiruChatRepository {
    val messages: Flow<List<TiruMessage>>
    suspend fun saveMessages(messages: List<TiruMessage>)
    suspend fun clearMessages()
    val fabSeen: Flow<Boolean>
    suspend fun setFabSeen(seen: Boolean)
}
