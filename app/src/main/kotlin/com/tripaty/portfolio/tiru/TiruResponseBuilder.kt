package com.tripaty.portfolio.tiru

import com.tripaty.portfolio.domain.model.Portfolio

object TiruResponseBuilder {

    fun build(intent: TiruIntent, userText: String, portfolio: Portfolio): String {
        val normalized = userText.lowercase()
        return when (intent) {
            TiruIntent.GREETING -> greeting()
            TiruIntent.EXPERIENCE -> experience(portfolio)
            TiruIntent.PROJECTS -> projects(normalized, portfolio)
            TiruIntent.SKILLS -> skills(normalized, portfolio)
            TiruIntent.CERTIFICATIONS -> certifications(portfolio)
            TiruIntent.CONTACT -> contact(portfolio)
            TiruIntent.LEADERSHIP -> leadership(portfolio)
            TiruIntent.AI -> ai(portfolio)
            TiruIntent.TESTIMONIALS -> testimonials(portfolio)
            TiruIntent.AIRLINE -> domain("airline", portfolio)
            TiruIntent.BANKING -> domain("banking", portfolio)
            TiruIntent.ENTERPRISE -> domain("enterprise", portfolio)
            TiruIntent.RESUME -> resume(portfolio)
            TiruIntent.FALLBACK -> fallback(portfolio)
        }
    }

    private fun greeting() =
        "Hi! I'm Tiru, Tripaty's portfolio assistant.\n\n" +
            "I can answer questions about his experience, projects, skills, certifications, and how to get in touch.\n\n" +
            "What would you like to know?"

    private fun experience(p: Portfolio): String {
        val profile = p.profile
        val stats = p.stats
        val jobs = p.experience.joinToString("\n") { job ->
            "- ${job.role} at ${job.company} (${job.dates}) - ${job.highlights}"
        }
        return "${profile.name} is an ${profile.title} based in ${profile.location} with ${stats.years} years of experience leading Android teams of up to ${stats.teamSize} engineers.\n\nCareer highlights:\n$jobs"
    }

    private fun projects(query: String, p: Portfolio): String {
        var studies = p.caseStudies
        val matched = studies.filter { cs ->
            cs.keywords.any { query.contains(it) } ||
                cs.project.lowercase().split(' ').any { it.length > 3 && query.contains(it) } ||
                cs.company.lowercase().split(' ').any { it.length > 3 && query.contains(it) }
        }
        if (matched.isNotEmpty()) studies = matched
        val limit = if (matched.isNotEmpty()) matched.size else 2
        val body = studies.take(limit).joinToString("\n\n") { cs ->
            buildString {
                append("${cs.project} (${cs.company})\n")
                append("Problem: ${cs.problem}\n")
                append("Action: ${cs.action}\n")
                append("Result: ${cs.result}")
                if (!cs.link.isNullOrBlank()) append("\nPlay Store: ${cs.link}")
            }
        }
        return "Here are Tripaty's key project achievements:\n\n$body"
    }

    private fun skills(query: String, p: Portfolio): String {
        val categories = linkedMapOf(
            "Leadership & Delivery" to (p.skills.leadership to listOf("lead", "manage", "people", "okr", "hiring", "agile", "delivery")),
            "Android Engineering" to (p.skills.android to listOf("kotlin", "compose", "android", "mvvm", "architecture", "mobile")),
            "Platform & Quality" to (p.skills.platform to listOf("ci", "cd", "test", "firebase", "api", "graphql", "room")),
            "AI-Assisted Engineering" to (p.skills.ai to listOf("ai", "copilot", "genai", "claude", "gemini")),
        )
        var filtered = categories.keys.toList()
        val matched = categories.filter { (_, pair) -> pair.second.any { query.contains(it) } }.keys
        if (matched.isNotEmpty()) filtered = matched.toList()
        val body = filtered.joinToString("\n\n") { label ->
            val tags = categories[label]!!.first.take(8).joinToString(", ")
            "$label: $tags"
        }
        return "Tripaty's core skills:\n\n$body"
    }

    private fun certifications(p: Portfolio): String {
        val items = p.certifications.joinToString("\n") { cert ->
            "- ${cert.name} - ${cert.issuer} (${cert.date})"
        }
        return "Tripaty holds ${p.certifications.size} certifications:\n$items"
    }

    private fun contact(p: Portfolio): String {
        val profile = p.profile
        return "Here's how to reach Tripaty:\n\n" +
            "Email: ${profile.email}\n" +
            "LinkedIn: ${profile.linkedin}\n" +
            "Phone: ${profile.phone}\n\n" +
            profile.availability
    }

    private fun testimonials(p: Portfolio): String {
        val items = p.testimonials.take(2).joinToString("\n\n") { t ->
            "\"${t.quote}\"\n- ${t.name}, ${t.title}, ${t.company}"
        }
        return "What colleagues say about Tripaty's leadership:\n\n$items"
    }

    private fun domain(domain: String, p: Portfolio): String {
        val keywords = when (domain) {
            "airline" -> listOf("qatar", "airline", "airways", "aviation", "travel")
            "banking" -> listOf("npci", "payment", "nfc", "banking")
            else -> listOf("campbell", "cintas", "dynamics", "enterprise", "star")
        }
        val label = when (domain) {
            "airline" -> "airline"
            "banking" -> "banking and payments"
            else -> "enterprise"
        }
        val studies = p.caseStudies.filter { cs -> cs.keywords.any { it in keywords } }
        val body = if (studies.isEmpty()) {
            "See case studies in the Work tab for details."
        } else {
            studies.joinToString("\n") { "${it.project} (${it.company}) - ${it.result}" }
        }
        return "Yes - Tripaty has $label experience:\n\n$body"
    }

    private fun resume(p: Portfolio): String =
        "${p.faqs.hiring}\n\nDownload resume from the More tab or visit ${p.profile.linkedin}"

    private fun leadership(p: Portfolio): String =
        "${p.faqs.leadership}\n\nAt NTT DATA, Tripaty currently leads teams of 8-10 Android engineers across enterprise programs serving ${p.stats.users.lowercase()} of users."

    private fun ai(p: Portfolio): String {
        val tools = p.skills.ai.joinToString(", ")
        return "${p.faqs.ai}\n\nTools: $tools"
    }

    private fun fallback(p: Portfolio): String {
        val prompts = p.quickPrompts.joinToString(", ") { "\"$it\"" }
        return "I'm not sure about that one, but I can help with experience, projects, skills, certifications, or contact info.\n\nTry asking: $prompts"
    }
}
