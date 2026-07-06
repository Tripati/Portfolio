package com.tripaty.portfolio.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PortfolioDto(
    val profile: ProfileDto,
    val stats: StatsDto,
    val skills: SkillsDto,
    val experience: List<ExperienceDto>,
    val caseStudies: List<CaseStudyDto>,
    val testimonials: List<TestimonialDto>,
    val certifications: List<CertificationDto>,
    val faqs: FaqsDto,
    val quickPrompts: List<String>,
    val site: SiteDto,
)

@Serializable data class ProfileDto(
    val name: String,
    val title: String,
    val location: String,
    val email: String,
    val linkedin: String,
    val phone: String,
    val availability: String,
    val resumeUrl: String,
    val summary: String,
)

@Serializable data class StatsDto(
    val years: String,
    val teamSize: Int,
    val users: String,
    val certifications: Int,
    val domains: List<String>,
)

@Serializable data class SkillsDto(
    val leadership: List<String>,
    val android: List<String>,
    val platform: List<String>,
    val ai: List<String>,
)

@Serializable data class ExperienceDto(
    val company: String,
    val role: String,
    val dates: String,
    val location: String? = null,
    val highlights: String,
)

@Serializable data class CaseStudyDto(
    val project: String,
    val company: String,
    val role: String,
    val keywords: List<String>,
    val problem: String,
    val action: String,
    val result: String,
    val link: String? = null,
    val sectionId: String,
)

@Serializable data class TestimonialDto(
    val quote: String,
    val name: String,
    val title: String,
    val company: String,
)

@Serializable data class CertificationDto(
    val name: String,
    val issuer: String,
    val date: String,
    val url: String,
)

@Serializable data class FaqsDto(
    val hiring: String,
    val leadership: String,
    val techStack: String,
    val ai: String,
)

@Serializable data class SiteDto(val plausibleDomain: String)
