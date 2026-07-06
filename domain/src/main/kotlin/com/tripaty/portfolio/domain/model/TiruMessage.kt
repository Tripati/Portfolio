package com.tripaty.portfolio.domain.model

data class TiruMessage(
    val role: TiruRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class TiruRole { USER, TIRU }
