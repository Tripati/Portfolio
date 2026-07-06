package com.tripaty.portfolio.data.mapper

import com.tripaty.portfolio.data.remote.*
import com.tripaty.portfolio.domain.model.*

fun PortfolioDto.toDomain() = Portfolio(
    profile = profile.toDomain(),
    stats = stats.toDomain(),
    skills = skills.toDomain(),
    experience = experience.map { it.toDomain() },
    caseStudies = caseStudies.map { it.toDomain() },
    testimonials = testimonials.map { it.toDomain() },
    certifications = certifications.map { it.toDomain() },
    faqs = faqs.toDomain(),
    quickPrompts = quickPrompts,
    site = site.toDomain(),
)

private fun ProfileDto.toDomain() = Profile(name, title, location, email, linkedin, phone, availability, resumeUrl, summary)
private fun StatsDto.toDomain() = Stats(years, teamSize, users, certifications, domains)
private fun SkillsDto.toDomain() = Skills(leadership, android, platform, ai)
private fun ExperienceDto.toDomain() = ExperienceItem(company, role, dates, location, highlights)
private fun CaseStudyDto.toDomain() = CaseStudy(project, company, role, keywords, problem, action, result, link, sectionId)
private fun TestimonialDto.toDomain() = Testimonial(quote, name, title, company)
private fun CertificationDto.toDomain() = Certification(name, issuer, date, url)
private fun FaqsDto.toDomain() = Faqs(hiring, leadership, techStack, ai)
private fun SiteDto.toDomain() = Site(plausibleDomain)
