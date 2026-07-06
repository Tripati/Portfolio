package com.tripaty.portfolio.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tripaty.portfolio.domain.model.TiruMessage
import com.tripaty.portfolio.domain.model.TiruRole
import com.tripaty.portfolio.domain.repository.TiruChatRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tiruDataStore: DataStore<Preferences> by preferencesDataStore("tiru_chat")

@Serializable
private data class StoredMessage(val role: String, val text: String, val timestamp: Long)

@Singleton
class TiruChatRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) : TiruChatRepository {

    private val messagesKey = stringPreferencesKey("messages")
    private val fabSeenKey = booleanPreferencesKey("fab_seen")

    override val messages: Flow<List<TiruMessage>> = context.tiruDataStore.data.map { prefs ->
        val raw = prefs[messagesKey] ?: return@map emptyList()
        runCatching {
            json.decodeFromString<List<StoredMessage>>(raw).map {
                TiruMessage(
                    role = if (it.role == "USER") TiruRole.USER else TiruRole.TIRU,
                    text = it.text,
                    timestamp = it.timestamp,
                )
            }
        }.getOrDefault(emptyList())
    }

    override suspend fun saveMessages(messages: List<TiruMessage>) {
        val stored = messages.map {
            StoredMessage(
                role = if (it.role == TiruRole.USER) "USER" else "TIRU",
                text = it.text,
                timestamp = it.timestamp,
            )
        }
        context.tiruDataStore.edit { it[messagesKey] = json.encodeToString(stored) }
    }

    override suspend fun clearMessages() {
        context.tiruDataStore.edit { it.remove(messagesKey) }
    }

    override val fabSeen: Flow<Boolean> = context.tiruDataStore.data.map { it[fabSeenKey] ?: false }

    override suspend fun setFabSeen(seen: Boolean) {
        context.tiruDataStore.edit { it[fabSeenKey] = seen }
    }
}
