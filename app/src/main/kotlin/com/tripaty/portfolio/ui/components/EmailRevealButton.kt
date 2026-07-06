package com.tripaty.portfolio.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tripaty.portfolio.util.openMailTo

@Composable
fun EmailRevealButton(
    email: String,
    revealed: Boolean,
    onReveal: () -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
) {
    val context = LocalContext.current
    val label = if (revealed) email else "Email"
    val onClick = {
        if (revealed) openMailTo(context, email) else onReveal()
    }
    if (outlined) {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(label) }
    } else {
        Button(onClick = onClick, modifier = modifier) { Text(label) }
    }
}
