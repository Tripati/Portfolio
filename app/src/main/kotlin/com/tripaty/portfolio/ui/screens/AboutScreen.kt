package com.tripaty.portfolio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.tripaty.portfolio.ui.components.EmailRevealButton
import com.tripaty.portfolio.ui.components.StatCard
import com.tripaty.portfolio.ui.theme.AccentBlue
import com.tripaty.portfolio.ui.theme.GrayText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    portfolio: Portfolio,
    emailRevealed: Boolean,
    onEmailReveal: () -> Unit,
    onLinkedIn: () -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(portfolio.profile.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(portfolio.profile.title, style = MaterialTheme.typography.titleMedium, color = AccentBlue)
                Text(portfolio.profile.location, style = MaterialTheme.typography.bodyMedium, color = GrayText)
                AssistChip(onClick = {}, label = { Text(portfolio.profile.availability) })
            }
        }
        item {
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Years", portfolio.stats.years, Modifier.weight(1f))
                StatCard("Team", "${portfolio.stats.teamSize}+", Modifier.weight(1f))
            }
        }
        item {
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard("Users", portfolio.stats.users, Modifier.weight(1f))
                StatCard("Certs", "${portfolio.stats.certifications}", Modifier.weight(1f))
            }
        }
        item {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Text(
                    portfolio.profile.summary,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                portfolio.stats.domains.forEach { domain ->
                    AssistChip(onClick = {}, label = { Text(domain) })
                }
            }
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EmailRevealButton(portfolio.profile.email, emailRevealed, onEmailReveal)
                androidx.compose.material3.OutlinedButton(onClick = onLinkedIn) { Text("LinkedIn") }
            }
        }
    }
}
