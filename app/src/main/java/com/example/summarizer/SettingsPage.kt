package com.example.summarizer

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val openUrlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}
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
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                PrivacyPolicySection(openUrlLauncher)
                ResourcesUsedSection(openUrlLauncher)
            }
        }
    )
}




@Composable
fun PrivacyPolicySection(openUrlLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    Text(
        text = "Privacy Policy",
        fontSize = 18.sp,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://sites.google.com/view/falcon-summarizer/home")
                openUrlLauncher.launch(intent)
            }
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ResourcesUsedSection(openUrlLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    Text(
        text = "Resources Used",
        fontSize = 18.sp,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://sites.google.com/view/falcon-summarizer/resources-used")
                openUrlLauncher.launch(intent)
            }
            .padding(0.dp, 10.dp)
            .fillMaxWidth()

    )
}


fun openResourcesUsed() {
    // TODO: Replace the URL with your privacy policy link
    val privacyPolicyUrl = "https://www.example.com/privacy-policy"
    // Launch the default browser to open the privacy policy link
    // Replace `context` with the appropriate Android context if needed
    // context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)))
    // Note: This code will not work inside the Compose function directly, it's just a placeholder for the behavior.
}
