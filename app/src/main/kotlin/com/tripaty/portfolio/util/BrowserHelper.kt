package com.tripaty.portfolio.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun openCustomTab(context: Context, url: String) {
    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(context, Uri.parse(url))
}

fun openMailTo(context: Context, email: String) {
    context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
}

fun openPhone(context: Context, phone: String) {
    val digits = phone.filter { it.isDigit() || it == '+' }
    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$digits")))
}
