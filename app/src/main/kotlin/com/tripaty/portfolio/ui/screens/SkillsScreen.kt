package com.tripaty.portfolio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tripaty.portfolio.domain.model.Portfolio
import com.tripaty.portfolio.ui.theme.AccentBlue

private data class SkillGroup(val title: String, val items: List<String>)

@Composable
fun SkillsScreen(portfolio: Portfolio, contentPadding: PaddingValues) {
    val groups = listOf(
        SkillGroup("Leadership & Delivery", portfolio.skills.leadership),
        SkillGroup("Android Engineering", portfolio.skills.android),
        SkillGroup("Platform & Quality", portfolio.skills.platform),
        SkillGroup("AI-Assisted Engineering", portfolio.skills.ai),
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(groups) { group ->
            SkillGroupCard(group)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillGroupCard(group: SkillGroup) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        ColumnSection(group)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnSection(group: SkillGroup) {
    androidx.compose.foundation.layout.Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(group.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = AccentBlue)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            group.items.forEach { skill ->
                AssistChip(onClick = {}, label = { Text(skill) })
            }
        }
    }
}
