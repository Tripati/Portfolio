package com.tripaty.portfolio.tiru

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripaty.portfolio.domain.model.TiruMessage
import com.tripaty.portfolio.domain.model.TiruRole
import com.tripaty.portfolio.domain.repository.PortfolioRepository
import com.tripaty.portfolio.domain.repository.TiruChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import javax.inject.Inject

data class TiruUiState(
    val messages: List<TiruMessage> = emptyList(),
    val isTyping: Boolean = false,
    val showPrompts: Boolean = true,
    val fabSeen: Boolean = false,
)

@HiltViewModel
class TiruViewModel @Inject constructor(
    private val chatRepository: TiruChatRepository,
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TiruUiState())
    val uiState: StateFlow<TiruUiState> = _uiState.asStateFlow()

    val storedMessages = chatRepository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            chatRepository.fabSeen.collect { seen ->
                _uiState.update { it.copy(fabSeen = seen) }
            }
        }
        viewModelScope.launch {
            chatRepository.messages.collect { messages ->
                if (messages.isNotEmpty()) {
                    _uiState.update { it.copy(messages = messages, showPrompts = false) }
                }
            }
        }
    }

    fun onFabClick() {
        viewModelScope.launch {
            if (!_uiState.value.fabSeen) {
                chatRepository.setFabSeen(true)
            }
        }
    }

    fun showWelcomeIfEmpty() {
        if (_uiState.value.messages.isEmpty()) {
            val welcome = TiruMessage(TiruRole.TIRU, TiruResponseBuilder.build(TiruIntent.GREETING, "", defaultPortfolio()))
            _uiState.update { it.copy(messages = listOf(welcome), showPrompts = true) }
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _uiState.value.isTyping) return

        val userMsg = TiruMessage(TiruRole.USER, trimmed)
        val updated = _uiState.value.messages + userMsg
        _uiState.update { it.copy(messages = updated, showPrompts = false, isTyping = true) }
        persist(updated)

        viewModelScope.launch {
            val delayMs = 300L + Random.nextLong(300)
            delay(delayMs)
            val portfolio = portfolioRepository.cachedPortfolio() ?: portfolioRepository.refresh()
                ?: defaultPortfolio()
            val intent = TiruIntentMatcher.match(trimmed)
            val reply = TiruResponseBuilder.build(intent, trimmed, portfolio)
            val withReply = updated + TiruMessage(TiruRole.TIRU, reply)
            _uiState.update { it.copy(messages = withReply, isTyping = false) }
            persist(withReply)
        }
    }

    fun clearChat() {
        if (_uiState.value.isTyping) return
        viewModelScope.launch {
            chatRepository.clearMessages()
            val welcome = TiruMessage(TiruRole.TIRU, TiruResponseBuilder.build(TiruIntent.GREETING, "", defaultPortfolio()))
            _uiState.update { it.copy(messages = listOf(welcome), showPrompts = true) }
            persist(listOf(welcome))
        }
    }

    private fun persist(messages: List<TiruMessage>) {
        viewModelScope.launch { chatRepository.saveMessages(messages) }
    }

    private fun defaultPortfolio() = portfolioRepository.cachedPortfolio()
        ?: com.tripaty.portfolio.domain.model.Portfolio(
            profile = com.tripaty.portfolio.domain.model.Profile(
                "Tripaty Kumar Sahu", "Engineering Manager", "Bengaluru", "tripati1987@gmail.com",
                "https://www.linkedin.com/in/tripaty-kumar-sahu-07a8732b/", "+91-8792637854",
                "Open to opportunities", "resume.pdf", ""
            ),
            stats = com.tripaty.portfolio.domain.model.Stats("14+", 10, "Millions", 7, emptyList()),
            skills = com.tripaty.portfolio.domain.model.Skills(emptyList(), emptyList(), emptyList(), emptyList()),
            experience = emptyList(), caseStudies = emptyList(), testimonials = emptyList(),
            certifications = emptyList(),
            faqs = com.tripaty.portfolio.domain.model.Faqs("", "", "", ""),
            quickPrompts = listOf("Experience?", "Key projects?", "Certifications?", "How to contact?"),
            site = com.tripaty.portfolio.domain.model.Site("tripati.github.io"),
        )
}
