package com.example.summarizer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.core.content.ContextCompat.startActivity
import com.falcon.summarizer.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

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
                backgroundColor = Color.White,
                elevation = 0.dp
            )
        },
        content = {
            it.calculateBottomPadding()
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                SectionOpenLinks(openUrlLauncher, "https://sites.google.com/view/falcon-summarizer/home", "Privacy Policy")
                SectionOpenLinks(openUrlLauncher, "https://sites.google.com/view/falcon-summarizer/resources-used", "Resources Used")
                ContactUsBugReportSection(openUrlLauncher, context, "Regarding App ", "Contact Us")
                ContactUsBugReportSection(openUrlLauncher, context, "Bug Report For ", "Bug Report")
                OpenOpenSourceLibraryActivity(openUrlLauncher, "Open Source Libraries")
                AboutSection()
            }
        }
    )
}

@Composable
fun AboutSection() {
    Text(
        text = "The note-taking app for Android is a powerful tool designed to help users easily capture, organize, and retrieve their notes on-the-go. With its user-friendly interface and advanced features such as voice-to-text, image insertion, and synchronization across multiple devices, it is the perfect solution for anyone looking to stay organized and productive.",
        fontSize = 18.sp,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
    )
}

@Composable
fun ContactUsBugReportSection(
    openUrlLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    context: Context,
    title: String,
    text: String
) {
    Text(
        text = text,
        fontSize = 18.sp,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:") // only email apps should handle this
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("usarcompanion@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, title + R.string.app_name)
                }
                try {
                    openUrlLauncher.launch(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast
                        .makeText(context, "No Mail App Found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
    )
}



@Composable
fun SectionOpenLinks(openUrlLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>, url: String, title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                openUrlLauncher.launch(intent)
            }
            .padding(0.dp, 10.dp)
            .fillMaxWidth()

    )
}


@Composable
fun OpenOpenSourceLibraryActivity(openUrlLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>, title: String) {
    val context = LocalContext.current
    Text(
        text = title,
        fontSize = 18.sp,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .clickable {
                startActivity(context, Intent(context, OssLicensesMenuActivity::class.java), null)
            }
            .padding(0.dp, 10.dp)
            .fillMaxWidth()
    )
}