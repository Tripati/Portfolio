package com.tripaty.portfolio.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tripaty.portfolio.domain.model.Portfolio
import com.tripaty.portfolio.domain.model.ThemeMode
import com.tripaty.portfolio.ui.components.EmailRevealButton
import com.tripaty.portfolio.ui.theme.GrayText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoreScreen(
    portfolio: Portfolio,
    emailRevealed: Boolean,
    themeMode: ThemeMode,
    onEmailReveal: () -> Unit,
    onLinkedIn: () -> Unit,
    onOpenCert: (String) -> Unit,
    onShareResume: () -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Certifications", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(portfolio.certifications) { cert ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onOpenCert(cert.url) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(cert.name, fontWeight = FontWeight.SemiBold)
                    Text("${cert.issuer} · ${cert.date}", style = MaterialTheme.typography.bodySmall, color = GrayText)
                }
            }
        }
        item {
            HorizontalDivider()
            Text("Testimonials", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(portfolio.testimonials) { t ->
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("\"${t.quote}\"", style = MaterialTheme.typography.bodyLarge)
                    Text("— ${t.name}, ${t.title}", style = MaterialTheme.typography.bodySmall, color = GrayText)
                    Text(t.company, style = MaterialTheme.typography.bodySmall, color = GrayText)
                }
            }
        }
        item {
            HorizontalDivider()
            Text("Connect", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Column(Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EmailRevealButton(portfolio.profile.email, emailRevealed, onEmailReveal, outlined = true, modifier = Modifier.fillMaxWidth())
                OutlinedButton(onClick = onLinkedIn, modifier = Modifier.fillMaxWidth()) { Text("LinkedIn") }
                OutlinedButton(onClick = onShareResume, modifier = Modifier.fillMaxWidth()) { Text("Share Resume") }
            }
        }
        item {
            HorizontalDivider()
            Text("Theme", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            FlowRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ThemeMode.entries.forEach { mode ->
                    FilterChip(
                        selected = themeMode == mode,
                        onClick = { onThemeChange(mode) },
                        label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    )
                }
            }
        }
    }
}
