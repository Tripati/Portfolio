package com.tripaty.portfolio.domain.model

data class Profile(
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

data class Stats(
    val years: String,
    val teamSize: Int,
    val users: String,
    val certifications: Int,
    val domains: List<String>,
)

data class Skills(
    val leadership: List<String>,
    val android: List<String>,
    val platform: List<String>,
    val ai: List<String>,
)

data class ExperienceItem(
    val company: String,
    val role: String,
    val dates: String,
    val location: String?,
    val highlights: String,
)

data class CaseStudy(
    val project: String,
    val company: String,
    val role: String,
    val keywords: List<String>,
    val problem: String,
    val action: String,
    val result: String,
    val link: String?,
    val sectionId: String,
)

data class Testimonial(
    val quote: String,
    val name: String,
    val title: String,
    val company: String,
)

data class Certification(
    val name: String,
    val issuer: String,
    val date: String,
    val url: String,
)

data class Faqs(
    val hiring: String,
    val leadership: String,
    val techStack: String,
    val ai: String,
)

data class Site(val plausibleDomain: String)

data class Portfolio(
    val profile: Profile,
    val stats: Stats,
    val skills: Skills,
    val experience: List<ExperienceItem>,
    val caseStudies: List<CaseStudy>,
    val testimonials: List<Testimonial>,
    val certifications: List<Certification>,
    val faqs: Faqs,
    val quickPrompts: List<String>,
    val site: Site,
)
