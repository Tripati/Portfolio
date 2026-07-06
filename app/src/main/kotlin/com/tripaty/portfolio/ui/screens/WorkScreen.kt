package com.tripaty.portfolio.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripaty.portfolio.domain.model.CaseStudy
import com.tripaty.portfolio.domain.model.ExperienceItem
import com.tripaty.portfolio.domain.model.Portfolio
import com.tripaty.portfolio.ui.theme.AccentGold
import com.tripaty.portfolio.ui.theme.GrayText
import com.tripaty.portfolio.ui.theme.PurpleSecondary

@Composable
fun WorkScreen(
    portfolio: Portfolio,
    onOpenLink: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Experience", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(portfolio.experience) { job ->
            ExperienceCard(job)
        }
        item {
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Text("Case Studies", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(portfolio.caseStudies) { study ->
            CaseStudyCard(study, onOpenLink)
        }
    }
}

@Composable
private fun ExperienceCard(job: ExperienceItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(job.role, fontWeight = FontWeight.SemiBold, color = PurpleSecondary)
            Text("${job.company} · ${job.dates}", style = MaterialTheme.typography.bodyMedium, color = GrayText)
            Text(job.highlights, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CaseStudyCard(study: CaseStudy, onOpenLink: (String) -> Unit) {
    var expanded by rememberSaveable(study.project) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(study.project, fontWeight = FontWeight.Bold)
                    Text("${study.company} · ${study.role}", style = MaterialTheme.typography.bodySmall, color = GrayText)
                }
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
            AnimatedVisibility(expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Problem", fontWeight = FontWeight.SemiBold, color = AccentGold)
                    Text(study.problem)
                    Text("Action", fontWeight = FontWeight.SemiBold, color = AccentGold)
                    Text(study.action)
                    Text("Result", fontWeight = FontWeight.SemiBold, color = AccentGold)
                    Text(study.result)
                    study.link?.let { link ->
                        Text("View on Play Store", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { onOpenLink(link) })
                    }
                }
            }
        }
    }
}
