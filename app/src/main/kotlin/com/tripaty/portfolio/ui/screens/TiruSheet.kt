package com.tripaty.portfolio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tripaty.portfolio.domain.model.Portfolio
import com.tripaty.portfolio.domain.model.TiruRole
import com.tripaty.portfolio.tiru.TiruUiState
import com.tripaty.portfolio.ui.theme.AccentBlue
import com.tripaty.portfolio.ui.theme.PurpleSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiruSheet(
    visible: Boolean,
    uiState: TiruUiState,
    portfolio: Portfolio?,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    onClear: () -> Unit,
    onShown: () -> Unit,
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(Unit) { onShown() }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(Modifier.fillMaxWidth().imePadding().padding(bottom = 16.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Tiru", style = MaterialTheme.typography.titleLarge)
                Row {
                    IconButton(onClick = onClear) { Icon(Icons.Default.Delete, contentDescription = "Clear chat") }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
            }
            val listState = rememberLazyListState()
            LaunchedEffect(uiState.messages.size, uiState.isTyping) {
                if (uiState.messages.isNotEmpty()) listState.animateScrollToItem(uiState.messages.lastIndex)
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f, fill = false).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.messages) { msg ->
                    val isUser = msg.role == TiruRole.USER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                    ) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isUser) PurpleSecondary else MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp),
                        ) {
                            Text(msg.text, color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                if (uiState.isTyping) {
                    item {
                        Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text(" Tiru is typing...", Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            if (uiState.showPrompts && portfolio != null) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    portfolio.quickPrompts.take(3).forEach { prompt ->
                        AssistChip(onClick = { onSend(prompt) }, label = { Text(prompt) })
                    }
                }
            }
            var input by remember { mutableStateOf("") }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask Tiru...") },
                    enabled = !uiState.isTyping,
                    maxLines = 3,
                )
                IconButton(
                    onClick = { if (input.isNotBlank()) { onSend(input); input = "" } },
                    enabled = !uiState.isTyping && input.isNotBlank(),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = AccentBlue)
                }
            }
        }
    }
}
