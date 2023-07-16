package com.example.summarizer

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun SettingsScreen() {
    var selectedFontSize by remember { mutableStateOf(16) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                backgroundColor = Color.White
            )
        },
        content = {
            it.calculateBottomPadding()
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                PrivacyPolicySection()
                ResourcesUsedSection()
            }
        }
    )
}



@Composable
fun PrivacyPolicySection() {
    Text(
        text = "Privacy Policy",
        fontSize = 20.sp,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                openPrivacyPolicy()
            }
    )
}

@Composable
fun ResourcesUsedSection() {
    Text(
        text = "Resources Used",
        fontSize = 20.sp,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                openResourcesUsed()
            }
    )
}

fun openPrivacyPolicy() {
    // TODO: Replace the URL with your privacy policy link
    val privacyPolicyUrl = "https://www.example.com/privacy-policy"
    // Launch the default browser to open the privacy policy link
    // Replace `context` with the appropriate Android context if needed
    // context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)))
    // Note: This code will not work inside the Compose function directly, it's just a placeholder for the behavior.
}

fun openResourcesUsed() {
    // TODO: Replace the URL with your privacy policy link
    val privacyPolicyUrl = "https://www.example.com/privacy-policy"
    // Launch the default browser to open the privacy policy link
    // Replace `context` with the appropriate Android context if needed
    // context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)))
    // Note: This code will not work inside the Compose function directly, it's just a placeholder for the behavior.
}
