package com.tripaty.portfolio.tiru

object TiruIntentMatcher {

    private data class IntentConfig(val keywords: List<String>, val weight: Double)

    private val intents = mapOf(
        TiruIntent.GREETING to IntentConfig(
            listOf("hi", "hello", "hey", "good morning", "good afternoon", "good evening", "howdy", "greetings"), 1.0
        ),
        TiruIntent.EXPERIENCE to IntentConfig(
            listOf("experience", "years", "background", "career", "work history", "resume", "cv", "timeline", "ntt", "itc"), 1.0
        ),
        TiruIntent.PROJECTS to IntentConfig(
            listOf("project", "projects", "qatar", "airline", "airways", "campbell", "star", "cintas", "dynamics", "npci", "payment", "case study", "achievement", "portfolio", "app"), 1.0
        ),
        TiruIntent.SKILLS to IntentConfig(
            listOf("skill", "skills", "kotlin", "compose", "jetpack", "android", "architecture", "mvvm", "technical", "stack", "expertise", "competenc"), 1.0
        ),
        TiruIntent.CERTIFICATIONS to IntentConfig(
            listOf("cert", "certification", "certified", "scrum", "csm", "copilot", "genai", "credential", "badge"), 1.0
        ),
        TiruIntent.CONTACT to IntentConfig(
            listOf("contact", "email", "linkedin", "phone", "hire", "reach", "connect", "call", "message", "talk"), 1.0
        ),
        TiruIntent.LEADERSHIP to IntentConfig(
            listOf("lead", "leader", "manage", "manager", "team", "okr", "hiring", "people", "1:1", "mentor", "style"), 1.0
        ),
        TiruIntent.AI to IntentConfig(
            listOf("ai", "copilot", "genai", "claude", "gemini", "artificial", "machine learning", "llm", "assistant"), 1.0
        ),
        TiruIntent.AIRLINE to IntentConfig(
            listOf("airline", "airways", "aviation", "travel", "amadeus", "checkmytrip"), 1.3
        ),
        TiruIntent.BANKING to IntentConfig(
            listOf("banking", "bank", "npci", "payment", "nfc", "hce", "finance", "fintech"), 1.3
        ),
        TiruIntent.ENTERPRISE to IntentConfig(
            listOf("enterprise", "b2b", "campbell", "cintas", "dynamics", "sap", "field service", "retail"), 1.2
        ),
        TiruIntent.RESUME to IntentConfig(
            listOf("resume", "cv", "download", "pdf", "document"), 1.4
        ),
        TiruIntent.TESTIMONIALS to IntentConfig(
            listOf("testimonial", "recommendation", "reference", "feedback", "review", "endorse"), 1.2
        ),
    )

    fun match(text: String): TiruIntent {
        val normalized = normalize(text)
        val words = normalized.split(" ")
        var best = TiruIntent.FALLBACK
        var bestScore = 0.0

        for ((intent, config) in intents) {
            var score = 0.0
            for (keyword in config.keywords) {
                if (normalized.contains(keyword)) {
                    score += if (keyword.contains(' ')) 2.0 else 1.0
                }
                for (word in words) {
                    if (word == keyword || (word.length > 3 && keyword.startsWith(word))) {
                        score += 0.5
                    }
                }
            }
            score *= config.weight
            if (score > bestScore) {
                bestScore = score
                best = intent
            }
        }
        return if (bestScore >= 1.0) best else TiruIntent.FALLBACK
    }

    private fun normalize(text: String): String =
        text.lowercase().replace(Regex("[^\\w\\s]"), " ").replace(Regex("\\s+"), " ").trim()
}
